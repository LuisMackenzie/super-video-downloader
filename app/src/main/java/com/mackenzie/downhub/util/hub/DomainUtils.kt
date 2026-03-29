package com.mackenzie.downhub.util.hub

import com.mackenzie.downhub.data.local.model.hub.ServerStatus
import com.mackenzie.downhub.data.local.model.hub.VideoItemType

internal fun getSFWUrlVideo(id: Int): String {
    return when (id % 5) {
        // 0 -> "https://test-streams.mux.dev/tos_ismc/main.m3u8" // Cohetes
        0 -> "https://gvideo.eporner.com/ZdRqusXAonP/ZdRqusXAonP.mp4" // Cohetes
        // 1 -> "https://test-streams.mux.dev/dai-discontinuity-deltatre/manifest.m3u8" // Sports Highlights
        1 -> "https://www.youporn.com/watch/190872831/" // Sports Highlights
        2 -> "https://sample.vodobox.net/skate_phantom_flex_4k/skate_phantom_flex_4k.m3u8" // Skate Phantom Flex 4K
        // 3 -> "https://content.jwplatform.com/manifests/vM7nH0Kl.m3u8" // Cohetes
        3 -> "https://video.beeg.com/key=uVIMsdI2+BXo5zmgpDO0CA,end=1774036791,limit=3/data=P9AKGE3ByX/media=hls4A/multi=426x240:240p:YXZjMS42NDAwMTUsbXA0YS40MC4y,640x360:360p:YXZjMS42NDAwMUUsbXA0YS40MC4y,854x480:480p:YXZjMS42NDAwMUUsbXA0YS40MC4y,1280x720:720p:YXZjMS42NDAwMUYsbXA0YS40MC4y,1920x1080:1080p:YXZjMS42NDAwMzIsbXA0YS40MC4y/_TPL_/734716432721532.mp4" // Cohetes
        else -> "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8" // Big Buck Bunny
    }
}

internal fun getServerStatus(id : Int): ServerStatus {
    return when (id) {
        50, 52, 53, 56, 58, 59, 61, 63, 64, 69 -> ServerStatus(isOffline = true)
        2, 3, 4, 5, 7, 8, 12, 15, 16, 18, 19, 54, 55, 57, 62, 67, 68 -> ServerStatus(canChargeList = true)
        1, 6, 9, 10, 11, 13, 14, 17, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 51, 60, 65, 66 -> ServerStatus(isFullyFunctional = true)
        else -> ServerStatus()
    }
}

internal fun getType(id: Int): VideoItemType {
    return when (id % 5) {
        0 -> VideoItemType.VIDEO
        1 -> VideoItemType.AUDIO
        else -> VideoItemType.PHOTO
    }
}

fun getEmbedUrl(serverId: Int, videoId: String): String {
    if (videoId.isEmpty()) return ""
    // TODO rellenar la seccion para actualizarla
    return when (serverId) {
        1 -> "https://es.pornhub.com/embed/$videoId"
        2 -> "https://es.redtube.com/embed/$videoId"
        3 -> "https://beeg.com/embed/0$videoId"
        4 -> "https://www.eporner.com/embed/$videoId/"
        5 -> "https://www.tube8.com/embed/$videoId"
        // 6 -> "https://www.xhamster.com/embed/$videoId" // parece que no tiene enlaces enbebidos
        7 -> "https://www.youjizz.com/videos/embed/$videoId"
        8 -> "https://www.youporn.com/embed/$videoId"
        9 -> "https://www.xvideos.com/embedframe/$videoId"
        10 -> "https://www.xnxx.com/embedframe/$videoId"
        11 -> "https://www.tnaflix.com/embed/$videoId"
        12 -> "https://blowjobs.pro/embed/$videoId"
        13 -> "https://spankbang.com/$videoId/embed/"
        14 -> "https://www.porntrex.com/embed/$videoId"
        15 -> "https://hqporner.com/embed/$videoId"
        16 -> "https://www.analdin.com/es/embed/$videoId"
        // 17 -> "https://www.xxxfiles.com/embed/$videoId" // No tiene enlaces embebidos
        18 -> "https://www.pornslash.com/embed/$videoId"
        19 -> "https://watchporn.to/embed/$videoId"

        20 -> "https://taboodude.com/video/$videoId"
        21 -> "https://www.freeuseporn.com/embed/$videoId"
        // 22 -> "PornXP"
        // 23 -> "Neporn"
        // 24 -> "They Are Huge" // No tiene enlaces embebidos
        25 -> "https://www.superporn.com/es/embed/$videoId"
        26 -> "https://www.peekvids.com/embed?v=$videoId"
        27 -> "https://beta.xfreehd.com/embed/$videoId"
        28 -> "https://www.trendyporn.com/embed/$videoId"
        29 -> "https://www.youcrazyx.com/embed/$videoId"

        else -> ""
    }
}

fun getNameById(id: Int): String {
    return when (id) {
        1 -> "PornHub"
        2 -> "RedTube"
        3 -> "Beeg"
        4 -> "Eporner"
        5 -> "Tube8"
        6 -> "XHamster"
        7 -> "YouJizz"
        8 -> "YouPorn"
        9 -> "XVideos"
        10 -> "XNXX"
        11 -> "TNAFLix"
        12 -> "Blowjobs"
        13 -> "SpangBang"
        14 -> "PornTrex"
        15 -> "HQPorner"
        16 -> "Analdin"
        17 -> "XXXFiles"
        18 -> "PornSlash"
        19 -> "WatchPorn"
        20 -> "TabooDude"
        21 -> "Free Use Porn"
        22 -> "PornXP"
        23 -> "Neporn"
        24 -> "They Are Huge"
        25 -> "Superporn"
        26 -> "Peekvids"
        27 -> "XfreeHD"
        28 -> "Trendy Porn"
        29 -> "YouCrazyX"

        50 -> "Hanime.tv"
        51 -> "HentaiCloud"
        52 -> "HentaiGasm"
        53 -> "HentaiMama 1"
        54 -> "HentaiMama 2"
        55 -> "HentaiMama 3"
        56 -> "HentaiTube 1"
        57 -> "HentaiTube 2"
        58 -> "HentaiPlay"
        59 -> "MuchoHentai"
        60 -> "Naughty Machinima"
        61 -> "OHentai"
        62 -> "PorCore"
        63 -> "xAnimePorn"
        64 -> "AniPorn"
        65 -> "ZZCartoon"
        66 -> "PornHub Hentai"
        67 -> "HentaiHeaven 1"
        68 -> "HentaiHeaven 2"
        69 -> "HentaiHeaven 3"

        80 -> "Chaturbate"
        81 -> "Amateur.tv"
        82 -> "BongaCams"
        83 -> "Cam4"
        84 -> "Camsoda"
        85 -> "CamWhoresBay"
        86 -> "StripChat"
        87 -> "Streamate"
        88 -> "SFW"
        else -> "Server Name Unknown"
    }
}

internal fun getServerUrlById(id: Int): String {
    return when (id) {
        1 -> "https://es.pornhub.com/video"  // Funciona !!
        2 -> "https://es.redtube.com/newest" // RedTube
        3 -> "https://beeg.com" // no reproduce por alguna razon rara
        4 -> "https://www.eporner.com" // tiene imagenes pero no reproduce videos
        5 -> "https://tube8.com" // Tube8 No funciona
        6 -> "https://xhamster.com/best/daily" // Funciona !! Pero solo muestra 5 videos
        7 -> "https://www.youjizz.com" // tiene imagenes pero no reproduce videos
        8 -> "https://www.youporn.com/most_viewed" // Funciona!!
        9 -> "https://www.xvideos.com/" // Funciona !!
        10 -> "https://www.xnxx.es/todays-selection"  // Funciona !!
        11 -> "https://www.tnaflix.com/new"  // TNAFLix No funciona
        12 -> "https://blowjobs.pro" // Blowjobs.pro No funciona
        13 -> "https://es.spankbang.com/" //  funciona !!
        14 -> "https://www.porntrex.com" // Funciona !!
        15 -> "https://hqporner.com" // HQPorner No funciona
        16 -> "https://www.analdin.com/" // Analdin No funciona
        17 -> "https://www.xxxfiles.com/latest-updates/" // Funciona !! Tiene boton de descarga directa
        18 -> "https://www.pornslash.com" // pornslash No funciona
        19 -> "https://watchporn.to/" // WatchPorn No funciona
        20 -> "https://taboodude.com/" // funciona !!
        21 -> "https://www.freeuseporn.com/" // Funciona !!
        22 -> "https://pornxp.ph/" // Funciona !!
        23 -> "https://neporn.com/latest-updates" // funciona !!
        24 -> "https://www.theyarehuge.com" // funcionoa !!
        25 -> "https://www.superporn.com/es" // funciiona !!
        26 -> "https://www.peekvids.com/Trending-Porn" // funciona !!
        27 -> "https://beta.xfreehd.com/trends/es" // funciona!!
        28 -> "https://www.trendyporn.com" // funciona !!!
        29 -> "https://www.youcrazyx.com" // funcional  !!!

        50 -> "https://hanime.tv/" // Hanime.tv
        51 -> "https://www.hentaicloud.com/" // HentaiCloud Funciona Bien ***
        52 -> "https://hentaigasm.com/" // HentaiGasm
        53 -> "https://hentaimama.io/" // HentaiMama
        54 -> "https://hentaimama.tv" // HentaiMama
        55 -> "https://hentaimama.xxx" // HentaiMama
        56 -> "https://www.hentaitube.online/" // HentaiTube
        57 -> "https://hentaitube.icu/" // HentaiTube
        58 -> "https://hentaiplay.net/" // HentaiPlay
        59 -> "https://muchohentai.com/home" // MuchoHentai
        60 -> "https://www.naughtymachinima.com/" // Naughty Machinima
        61 -> "https://ohentai.org" // OHentai
        62 -> "https://porcore.com" // PorCore
        63 -> "https://xanimeporn.com/" // xAnimePorn
        64 -> "https://aniporn.com/most-popular/" // AniPorn
        65 -> "https://www.zzcartoon.com" // ZZCartoon
        66 -> "https://es.pornhub.com/categories/hentai" // PornHub Hentai
        67 -> "https://hentaihaven.co/" // HentaiHeaven
        68 -> "https://hentaihaven.com/" // HentaiHeaven

        88 -> "https://es.redtube.com/newest" // SFW Mock channel, using RedTube as placeholder
        else -> ""
    }
}

internal fun getImageFromServerId(id: Int): String {
    return when (id) {
        1 -> "https://img.icons8.com/color/512/pornhub.png"  // PornHub
//        2 -> "https://static.wikia.nocookie.net/logopedia/images/a/ad/RedTube_2007_logo.png/revision/latest/scale-to-width-down/284?cb=20230616170943" // RedTube
        2 -> "https://ei.rdtcdn.com/www-static/cdn_files/redtube/icons/favicon.png?v=e7d648c8fed98bfcee2fbbd6f050e99c53bca70a" // RedTube
        3 -> "https://lh3.googleusercontent.com/YrLQ2iF13cX-RBTf-0iM5gBcDm3woauAzoT-AmMXGhRq-R48iBALY5lSDSy8ciMaoGN9" // Beeg
        4 -> "https://www.blackhatworld.com/data/avatars/o/1814/1814745.jpg?1695663155" // Eporner
        5 -> "https://firebounty.com/image/912-tube8" // Tube8
        6 -> "https://stripcash.com/blog/content/images/size/w2000/2024/12/2024-12-18_17-29-35.jpg" // XHamster
        7 -> "https://www.dafont.com/forum/attach/orig/8/0/806734.png?1" // YouJizz
        8 -> "https://static0.polygonimages.com/wordpress/wp-content/uploads/chorus/uploads/chorus_asset/file/15030072/youporn-logo.0.0.1485620315.jpg?q=50&fit=crop&w=608&h=342&dpr=1.5" // YouPorn
        9 -> "https://thumbs.dreamstime.com/b/xvideos-pornographic-video-sharing-viewing-website-december-most-visited-pornographic-website-according-142175224.jpg" // XVideos
        10 -> "https://i.redd.it/83qah8xpljee1.jpg"  // XNXX
        11 -> "https://static.semrush.com/power-pages/media/favicons/tnaflix-com-favicon-dab11f7b.png"  // TNAFLix
        // 12 -> "https://ih1.redbubble.net/image.5390711201.6861/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.jpg" // Blowjobs.pro
        12 -> "https://blowjobs.pro/favicon/android-icon-192x192.png" // Blowjobs.pro
        13 -> "https://spankbangs.co.uk/wp-content/uploads/2024/06/spankbang-com-favicon-17110d01.png" // SpangBang
        14 -> "https://ptx.cdntrex.com/contents/videos_screenshots/2848000/2848250/preview.jpg" // PornTrex
        15 -> "https://tse1.mm.bing.net/th?q=hqporner+com" // HQPorner
        16 -> "https://s3.eu-central-1.amazonaws.com/asg-mediakit-logos-prod/uploads/trafokit_website/logo/1/analdin__1_.jpg" // Analdin
        17 -> "https://cdn2.f-cdn.com/contestentries/1673227/33004112/5dda66473abbd_thumbCard.jpg" // XXXFiles
        18 -> "https://cdn.dribbble.com/userupload/28667207/file/still-c31818a5a085c00581c8f84caeaa4bf4.png?resize=400x0" // PornSlash
        19 -> "https://cbx-prod.b-cdn.net/COLOURBOX62623450.jpg?width=800&height=800&quality=70" // WatchPorn
        20 -> "https://taboodude.com/media/setting/a24b3o251675756396.png"
        21 -> "https://cdn-icons-png.flaticon.com/512/7135/7135961.png"
        22 -> "https://pornxp.ph/logo.png"
        23 -> "https://neporn.com/apple-touch-icon.png"
        24 -> "https://www.theyarehuge.com/static/images/tah-logo-m.png"
        25 -> "https://img6.superporn.com/videos/710/71066/thumbs/thumbs_0011_custom_1722699633.6203.jpg"
        26 -> "https://www.peekvids.com/img/logo.png"
        27 -> "https://beta.xfreehd.com/templates/frontend/xfreehd/img/logo.png"
        28 -> "https://pbs.twimg.com/profile_images/1054618246551298048/Dkj4DQSt_400x400.jpg"
        29 -> "https://logoeps.com/wp-content/uploads/2013/04/porn-star-vector-logo.png"

        50 -> "https://ih1.redbubble.net/image.2357425361.6067/raf,360x360,075,t,fafafa:ca443f4786.u2.jpg"  // Hanime.tv
        51 -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTrLP_RgKcH7Ws83XP9us0Tjdv0cewIFyk0ag&s" // HentaiCloud
        52 -> "https://hentaigasm.tv/wp-content/uploads/2024/12/HG.png" // HentaiGasm
        53 -> "https://hentaimama.tv/wp-content/uploads/2022/08/1-favicon_PNG.png" // HentaiMama
        54 -> "https://hentaimama.tv/wp-content/uploads/2022/08/1-favicon_PNG.png" // HentaiMama
        55 -> "https://hentaimama.tv/wp-content/uploads/2022/08/1-favicon_PNG.png" // HentaiMama
        56 -> "https://ih1.redbubble.net/image.1118561511.1643/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.jpg" // HentaiTube
        57 -> "https://ih1.redbubble.net/image.1118561511.1643/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.jpg" // HentaiTube
        58 -> "https://www.soviet-power.com/image/cache/data/2021/w165%20%D0%B0-550x550.jpg" // HentaiPlay
        59 -> "https://cdn2.steamgriddb.com/logo_thumb/3bbc8b8ac2f0e75cafa24ec9b9530352.png" // MuchoHentai
        60 -> "https://static.wikia.nocookie.net/logopedia/images/1/1c/Machinima.svg/revision/latest/scale-to-width-down/200?cb=20161120075354" // Naughty Machinima
        61 -> "https://pandatools.org/wp-content/uploads/2024/06/image-54.png" // OHentai
        62 -> "https://thumbs.dreamstime.com/b/hentai-rosette-stamp-imitation-grunge-style-designed-round-ribbon-small-crowns-blue-vector-rubber-print-text-texture-136328212.jpg" // PorCore
        63 -> "https://assets.thepornmap.com/wp-content/uploads/20251104195912/xanimeporn.png" // xAnimePorn
        64 -> "https://ei.rdtcdn.com/m=eOhlbe/media/pics/sites/006/590/561/cover1687211108/1687211108.jpg" // AniPorn
        65 -> "https://cdn.displate.com/artwork/380x270/2023-01-11/258b544708e5360b2a577e1311c67a40_ebb0719b47c2dcc503bbd57cb226caa8.jpg" // ZZCartoon
        66 -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQVC6zfQDXZ3GLXR2HebVdgj-n7vAdPB0jxbQ&s" // PornHub
        67 -> "https://preview.redd.it/bi0o1d5qw7y21.jpg?auto=webp&s=55351ad97b3ae9503562da00d2d96d62f858899f" // HentaiHeaven
        68 -> "https://preview.redd.it/bi0o1d5qw7y21.jpg?auto=webp&s=55351ad97b3ae9503562da00d2d96d62f858899f" // HentaiHeaven
        69 -> "https://preview.redd.it/bi0o1d5qw7y21.jpg?auto=webp&s=55351ad97b3ae9503562da00d2d96d62f858899f" // HentaiHeaven

        80 -> "https://logowik.com/content/uploads/images/chaturbate1720166505.logowik.com.webp" // Chaturbate
        81-> "https://www.kamastudioagencia.com/wp-content/uploads/2024/10/amateur-scaled.jpg"  // Amateur.tv
        82 -> "https://juanbustos.com/wp-content/uploads/2019/02/BongaCams_01-copia.jpg"  // BongaCams
        83 -> "https://webcamstartup.com/wp-content/uploads/2024/07/CAM4_Site_Logo.png"  // Cam4
        84 -> "https://play-lh.googleusercontent.com/BByJrJkoUsr1zl4-B16qjyfIlSZxvbiqaga27HCF_EebNkkQfIf2QgX4bXnWhBRMpF4"  // Camsoda
        85 -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTwCS5SYQKu0_tGhtO6d4sJuajxhBtobyCLgw&s" // CamWhoresBay
        86 -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQtMQA8Eky7VirE7acAwsscAqsOm3MrxSNvXg&s" // StripChat
        87 -> "https://media.licdn.com/dms/image/v2/C560BAQF0a5MYbE0T2g/company-logo_200_200/company-logo_200_200/0/1630645346067?e=2147483647&v=beta&t=cImXQFmlhNwHsGbfz9Hx80D4jcN9q-BpWm3XKdZ_3Is" // Streamate
        88 -> "https://media.licdn.com/dms/image/v2/C560BAQF0a5MYbE0T2g/company-logo_200_200/company-logo_200_200/0/1630645346067?e=2147483647&v=beta&t=cImXQFmlhNwHsGbfz9Hx80D4jcN9q-BpWm3XKdZ_3Is" // SFW Mock channel
        else -> "https://loremflickr.com/400/400/girl?lock=$id"
    }
}