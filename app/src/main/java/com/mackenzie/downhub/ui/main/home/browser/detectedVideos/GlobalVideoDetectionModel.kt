package com.mackenzie.downhub.ui.main.home.browser.detectedVideos

import android.os.Handler
import android.os.Looper
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import com.mackenzie.downhub.R
import com.mackenzie.downhub.data.local.room.entity.VideoInfo
import com.mackenzie.downhub.data.repository.VideoRepository
import com.mackenzie.downhub.ui.main.home.browser.DownloadButtonStateCanDownload
import com.mackenzie.downhub.ui.main.home.browser.DownloadButtonStateCanNotDownload
import com.mackenzie.downhub.ui.main.home.browser.DownloadButtonStateLoading
import com.mackenzie.downhub.util.AppLogger
import com.mackenzie.downhub.util.proxy_utils.OkHttpProxyClient
import com.mackenzie.downhub.util.scheduler.BaseSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.Request
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GlobalVideoDetectionModel @Inject constructor(
    private val videoRepository: VideoRepository,
    private val baseSchedulers: BaseSchedulers,
    okHttpProxyClient: OkHttpProxyClient,
) : VideoDetectionTabViewModel(videoRepository, baseSchedulers, okHttpProxyClient), IVideoDetector {
    private var lastVerifiedLink: String = ""
    private var lastVerifiedM3u8PointUrl = Pair("", "")

    private val butonStateCallBack = object :
        Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            when (downloadButtonState.get()) {
                is DownloadButtonStateCanNotDownload -> downloadButtonIcon.set(R.drawable.refresh_24px)
                is DownloadButtonStateCanDownload -> downloadButtonIcon.set(R.drawable.ic_download_24dp)
                is DownloadButtonStateLoading -> {
                    downloadButtonIcon.set(R.drawable.invisible_24px)
                }
            }
        }
    }

    override fun start() {
        downloadButtonState.addOnPropertyChangedCallback(butonStateCallBack)
    }

    override fun stop() {
        downloadButtonState.removeOnPropertyChangedCallback(butonStateCallBack)
        super.stop()
    }

    override fun cancelAllCheckJobs() {
        super.cancelAllCheckJobs()

        lastVerifiedLink = ""
    }

    override fun hasCheckLoadingsRegular(): ObservableBoolean {
        return ObservableBoolean(false)
    }

    override fun hasCheckLoadingsM3u8(): ObservableBoolean {
        return ObservableBoolean(false)
    }

    override fun verifyLinkStatus(
        resourceRequest: Request, hlsTitle: String?, isM3u8: Boolean, isMpd: Boolean
    ) {
        if (resourceRequest.url.toString().contains("tiktok.")) {
            return
        }

        val urlToVerify = resourceRequest.url.toString()

        if (lastVerifiedLink != urlToVerify) {
            val currentPageUrl = "${resourceRequest.url}"

            if (isM3u8 || isMpd) {
                if ((currentPageUrl == lastVerifiedM3u8PointUrl.first && lastVerifiedM3u8PointUrl.second != urlToVerify) || currentPageUrl != lastVerifiedM3u8PointUrl.first) {
                    lastVerifiedM3u8PointUrl = Pair(currentPageUrl, urlToVerify)

                    startVerifyProcess(resourceRequest, isM3u8, isMpd, hlsTitle)
                }
            } else {
                if (urlToVerify.contains(
                        ".txt"
                    )
                ) {
                    return
                }
                lastVerifiedLink = urlToVerify

                if (settingsModel.getIsFindVideoByUrl().get()) {
                    startVerifyProcess(resourceRequest, isM3u8 = false, isMpd = false)
                }
            }
        }
    }

    override fun startVerifyProcess(
        resourceRequest: Request, isM3u8: Boolean, isMpd: Boolean, hlsTitle: String?
    ) {
        val job = verifyVideoLinkJobStorage[resourceRequest.url.toString()]
        if (job != null && !job.isDisposed) {
            return
        }

        verifyVideoLinkJobStorage[resourceRequest.url.toString()] =
            io.reactivex.rxjava3.core.Observable.create { emitter ->
                downloadButtonState.set(DownloadButtonStateLoading())
                val info = try {
                    if (isM3u8 || isMpd) {
                        videoRepository.getVideoInfoBySuperXDetector(
                            resourceRequest,
                            isM3u8,
                            isMpd,
                            settingsModel.isCheckOnAudio.get()
                        )
                    } else {
                        videoRepository.getVideoInfo(
                            resourceRequest,
                            false,
                            settingsModel.isCheckOnAudio.get()
                        )
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    null
                }
                if (info != null) {
                    emitter.onNext(info)
                } else {
                    emitter.onNext(VideoInfo(id = ""))
                }
            }.observeOn(baseSchedulers.io).subscribeOn(baseSchedulers.io).subscribe { info ->
                val isLastNotEmpty = lastVerifiedLink.isNotEmpty()

                if (info.id.isNotEmpty()) {
                    if (info.isM3u8 && !hlsTitle.isNullOrEmpty()) {
                        info.title = hlsTitle
                    }
                    val state = downloadButtonState.get()
                    if (state is DownloadButtonStateCanDownload) {
                        if (state.info?.isRegularDownload == true) {
                            AppLogger.Companion.d(
                                "Watching set new info state with Regular Download... currentState: $state skippingInfo: $info"
                            )
                        }
                    }
                    if (state is DownloadButtonStateCanDownload && state.info?.isM3u8 == true && state.info.isMaster && isLastNotEmpty || (state is DownloadButtonStateCanDownload && state.info?.isRegularDownload != true && info.isRegularDownload) || state is DownloadButtonStateLoading && info.isRegularDownload) {
                        AppLogger.Companion.d(
                            "Skipping set new info state... currentState: $state skippingInfo: $info"
                        )
                    } else {
                        AppLogger.Companion.d(
                            "Setting set new info state... state: $state info: $info"
                        )
                        pushNewVideoInfoToAll(info)
                    }
                } else {
                    downloadButtonState.set(DownloadButtonStateCanNotDownload())
                }
            }
    }

    override fun checkRegularVideoOrAudio(
        request: Request?,
        isCheckOnAudio: Boolean,
        isCheckOnVideo: Boolean
    ): Disposable? {
        if (request == null) {
            return null
        }

        val uriString = request.url.toString()

        if (!uriString.startsWith("http")) {
            return null
        }

        val clearedUrl = uriString.split("?").first().trim()

        if (clearedUrl.contains(filterRegex)) {
            return null
        }

        val headers = try {
            request.headers.toMap().toMutableMap()
        } catch (_: Throwable) {
            mutableMapOf()
        }

        val disposable = io.reactivex.rxjava3.core.Observable.create<Unit> {
            propagateCheckJob(uriString, headers, isCheckOnAudio, isCheckOnVideo)
            it.onComplete()
        }.subscribeOn(baseSchedulers.io).doOnComplete {
            AppLogger.Companion.d("CHECK REGULAR MP4 IN BACKGROUND DONE")
        }.onErrorComplete().doOnError {
            AppLogger.Companion.d("Checking IN BACKGROUND ERROR... $clearedUrl")
        }.subscribe()

        return disposable
    }

    override fun pushNewVideoInfoToAll(newInfo: VideoInfo) {
        if (newInfo.formats.formats.isEmpty()) {
            return
        }

        downloadButtonState.set(DownloadButtonStateLoading())
        Handler(Looper.getMainLooper()).postDelayed({
            downloadButtonState.set(DownloadButtonStateCanDownload(newInfo))
        }, 400)
    }

    override fun onStartPage(url: String, userAgentString: String) {

    }

    override fun showVideoInfo() {

    }
}
