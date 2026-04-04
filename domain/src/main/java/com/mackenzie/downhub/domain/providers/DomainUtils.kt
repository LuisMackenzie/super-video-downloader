package com.mackenzie.downhub.domain.providers

import com.mackenzie.downhub.domain.ServerStatus
import com.mackenzie.downhub.domain.VideoItemType


internal fun getSFWUrlVideo(id: Int): String {
    return when (id % 5) {
        0 -> "https://test-streams.mux.dev/tos_ismc/main.m3u8" // Cohetes
        // 0 -> "https://gvideo.eporner.com/ZdRqusXAonP/ZdRqusXAonP.mp4" // ** prueba de video directo
        1 -> "https://test-streams.mux.dev/dai-discontinuity-deltatre/manifest.m3u8" // Sports Highlights
        // 1 -> "https://www.youporn.com/watch/190872831/" // ** prueba de video directo
        2 -> "https://sample.vodobox.net/skate_phantom_flex_4k/skate_phantom_flex_4k.m3u8" // Skate Phantom Flex 4K
        3 -> "https://content.jwplatform.com/manifests/vM7nH0Kl.m3u8" // Cohetes
        // 3 -> "https://video.beeg.com/key=uVIMsdI2+BXo5zmgpDO0CA,end=1774036791,limit=3/data=P9AKGE3ByX/media=hls4A/multi=426x240:240p:YXZjMS42NDAwMTUsbXA0YS40MC4y,640x360:360p:YXZjMS42NDAwMUUsbXA0YS40MC4y,854x480:480p:YXZjMS42NDAwMUUsbXA0YS40MC4y,1280x720:720p:YXZjMS42NDAwMUYsbXA0YS40MC4y,1920x1080:1080p:YXZjMS42NDAwMzIsbXA0YS40MC4y/_TPL_/734716432721532.mp4" // prueba de video directo
        else -> "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8" // Big Buck Bunny
    }
}

internal fun getServerStatus(id : Int): ServerStatus {
    return when (id) {
        200, 202, 203, 206, 208, 209, 211, 213, 214, 219 -> ServerStatus(isOffline = true)
        2, 3, 4, 5, 7, 8, 12, 15, 16, 18, 19, 204, 205, 207, 212, 217, 218 -> ServerStatus(canChargeList = true)
        1, 6, 9, 10, 11, 13, 14, 17, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 201, 210, 215, 216 -> ServerStatus(isFullyFunctional = true)
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
        30 -> "MotherLess"
        31 -> "XMoviesForYou"
        32 -> "YourPorn"
        33 -> "Porn One"
        34 -> "3movs"
        35 -> "PornDig"
        36 -> "CumLouder"
        37 -> "TXXX"
        38 -> "Porn Doe"
        39 -> "PornHat"
        40 -> "OK.xxx"
        41 -> "Porn00"
        42 -> "PornHoarder"
        43 -> "YesPornVip"
        44 -> "PornHits"
        45 -> "Porn GO"
        46 -> "WhoresHub"
        47 -> "Vsex"
        48 -> "Serviporno"
        49 -> "Poringa"
        50 -> "MuyZorras"
        51 -> "Porno Gratis diario"
        52 -> "Cerdas"
        53 -> "PornoReino"
        54 -> "XTapes"
        55 -> "PetardasHD"
        56 -> "LovinSiren"
        57 -> "VideosEgg"
        58 -> "Pulpo69"
        59 -> "Rubias19"
        60 -> "Porn.es"
        61 -> "Feliz Porno"
        62 -> "ConejoX"
        63 -> "Xorgasmo"
        64 -> "PelisXPorno"
        65 -> "Serakon"
        66 -> "PorNudes"
        67 -> "PerformerPedia"
        68 -> "PornHD3x"
        69 -> "PornDish"
        70 -> "Full Porner"
        71 -> "Porn4Days"
        72 -> "ParadiseHills"
        73 -> "PornHd8K"
        74 -> "Perfect Girls"
        75 -> "Your daily porn videos"
        76 -> "EroMe"
        77 -> "Porn300"
        78 -> "AnySex"
        79 -> "VXXX"
        80 -> "VePorn"
        81 -> "DrTuber"
        82 -> "NetFapx"
        83 -> "LetsJerk"
        84 -> "PornoBae"
        85 -> "PornoMz"
        86 -> "XMegaDrive"
        87 -> "SeverePorn"
        88 -> "Hitprn"
        89 -> "CzechVideo"
        90 -> "WatchXXXFree"
        91 -> "HDPorn92"
        92 -> "YesPornPlease"
        93 -> "Reddit.tube"
        94 -> "FUXNXX"
        95 -> "4KPorn"
        96 -> "InPorn"
        97 -> "PornTop"
        98 -> "Anyporn"
        99 -> "LatestPornVideos"
        100 -> "BananaMovies"


        200 -> "Hanime.tv"
        201 -> "HentaiCloud"
        202 -> "HentaiGasm"
        203 -> "HentaiMama 1"
        204 -> "HentaiMama 2"
        205 -> "HentaiMama 3"
        206 -> "HentaiTube 1"
        207 -> "HentaiTube 2"
        208 -> "HentaiPlay"
        209 -> "MuchoHentai"
        210 -> "Naughty Machinima"
        211 -> "OHentai"
        212 -> "PorCore"
        213 -> "xAnimePorn"
        214 -> "AniPorn"
        215 -> "ZZCartoon"
        216 -> "PornHub Hentai"
        217 -> "HentaiHeaven 1"
        218 -> "HentaiHeaven 2"
        219 -> "HentaiHeaven 3"

        250 -> "Chaturbate"
        251 -> "Amateur.tv"
        252 -> "BongaCams"
        253 -> "Cam4"
        254 -> "Camsoda"
        255 -> "CamSter"
        256 -> "StripChat"
        257 -> "Streamate"
        258 -> "SFW"
        259 -> "Live Jasmin"
        260 -> "BabeStation"
        261 -> "IMLive"
        262 -> "JerkMate"
        263 -> "MyFreecams"
        264 -> "OleCams"
        265 -> "VideoChatErotico"
        266 -> "ObsexionCams"
        267 -> "FLirtBate"
        268 -> "XloveCam"
        269 -> "SkyPrivate"
        270 -> "Coomeet"
        271 -> "XCams"
        272 -> "Flirtify"
        273 -> "SexPanther"
        274 -> "SinParty"
        275 -> "Cams.com"
        276 -> "SakuraLive"
        277 -> "SlutRoullete"
        278 -> "LuckyCrush"
        279 -> "Xtease"
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
        30 -> "https://motherless.com/"
        31 -> "https://xmoviesforyou.com/"
        32 -> "https://sxyprn.com/"
        33 -> "https://pornone.com/"
        34 -> "https://www.3movs.com/"
        35 -> "https://www.porndig.com/"
        36 -> "https://www.cumlouder.com/"
        37 -> "https://txxx.com/"
        38 -> "https://porndoe.com/"
        39 -> "https://www.pornhat.com/"
        40 -> "https://ok.xxx/"
        41 -> "https://www.porn00.org/latest-vids/"
        42 -> "https://pornhoarder.tw/"
        43 -> "https://yesporn.vip/"
        44 -> "https://www.pornhits.com/main/"
        45 -> "https://www.porngo.com/"
        46 -> "https://www.whoreshub.com/"
        47 -> "https://vsex.in/"
        48 -> "https://www.serviporno.com/"
        49 -> "https://www.poringa.net/"
        50 -> "https://www.muyzorras.com/"
        51 -> "https://www.pornogratisdiario.com/"
        52 -> "https://www.cerdas.com/"
        53 -> "https://www.pornoreino.com/"
        54 -> "https://xtapes.tw/"
        55 -> "http://petardashd.com/"
        56 -> "https://lovingsiren.com/"
        57 -> "https://videosegg.com/"
        58 -> "https://pulpo69.com/"
        59 -> "https://rubias19.com/"
        60 -> "https://pornes.xxx/"
        61 -> "https://www.felizporno.com/"
        62 -> "https://conejox.com/"
        63 -> "https://xorgasmo.com/"
        64 -> "https://www.pelisxporno.net/"
        65 -> "https://serakon.com/"
        66 -> "https://www.pornudes.com/"
        67 -> "https://www.performerpedia.com/"
        68 -> "https://www.pornhd3x.tv/"
        69 -> "https://www.porndish.com/"
        70 -> "https://fullporner.com/"
        71 -> "https://porn4days.pw/"
        72 -> "https://paradisehill.cc/porn/"
        73 -> "https://en8.pornhd8k.net/"
        74 -> "https://www.perfectgirls.xxx/"
        75 -> "https://yourdailypornvideos.ws/"
        76 -> "https://www.erome.com/explore"
        77 -> "https://www.porn300.com/"
        78 -> "https://anysex.com/"
        79 -> "https://vxxx.com/"
        80 -> "https://veporn.com/"
        81 -> "https://www.drtuber.com/"
        82 -> "https://netfapx.com/"
        83 -> "https://letsjerk.tv/"
        84 -> "https://pornobae.com/"
        85 -> "https://pornmz.com/"
        86 -> "https://www.xmegadrive.com/"
        87 -> "https://severeporn.com/"
        88 -> "https://www.hitprn.com/"
        89 -> "https://czechvideo.ac/"
        90 -> "https://xxxfree.watch/"
        91 -> "https://hdporn92.com/"
        92 -> "https://yespornpleasexxx.com/"
        93 -> "https://www.reddit.tube/nsfw"
        94 -> "https://foxnxx.com/"
        95 -> "https://4kporn.xxx/"
        96 -> "https://inporn.com/"
        97 -> "https://porntop.com/"
        98 -> "https://anyporn.com/categories/hd/"
        99 -> "https://latestpornvideo.com/"
        100 -> "https://bananamovies.org/"

        200 -> "https://hanime.tv/" // Hanime.tv
        201 -> "https://www.hentaicloud.com/" // HentaiCloud Funciona Bien ***
        202 -> "https://hentaigasm.com/" // HentaiGasm
        203 -> "https://hentaimama.io/" // HentaiMama
        204 -> "https://hentaimama.tv" // HentaiMama
        205 -> "https://hentaimama.xxx" // HentaiMama
        206 -> "https://www.hentaitube.online/" // HentaiTube
        207 -> "https://hentaitube.icu/" // HentaiTube
        208 -> "https://hentaiplay.net/" // HentaiPlay
        209 -> "https://muchohentai.com/home" // MuchoHentai
        210 -> "https://www.naughtymachinima.com/" // Naughty Machinima
        211 -> "https://ohentai.org" // OHentai
        212 -> "https://porcore.com" // PorCore
        213 -> "https://xanimeporn.com/" // xAnimePorn
        214 -> "https://aniporn.com/most-popular/" // AniPorn
        215 -> "https://www.zzcartoon.com" // ZZCartoon
        216 -> "https://es.pornhub.com/categories/hentai" // PornHub Hentai
        217 -> "https://hentaihaven.co/" // HentaiHeaven
        218 -> "https://hentaihaven.com/" // HentaiHeaven

        250 -> "https://chaturbate.com/"
        251 -> "https://es.amateur.tv/"
        252 -> "https://www.bongacams.com/"
        253 -> "https://es.cam4.com/"
        254 -> "https://www.camsoda.com/"
        255 -> "https://www.camster.com/"
        256 -> "https://es.stripchat.com/"
        257 -> "https://streamate.com/?AFNO=2-17499"
        258 -> "https://es.redtube.com/newest" // SFW Mock channel, using RedTube as placeholder
        259 -> "https://www.livejasmin.com/"
        260 -> "https://www.babestation.tv/"
        261 -> "https://imlive.com/"
        262 -> "https://jerkmate.com/"
        263 -> "https://www.myfreecams.com/"
        264 -> "https://www.olecams.tv/"
        265 -> "https://www.videochaterotico.com/"
        266 -> "https://obsexioncams.com/"
        267 -> "https://flirtbate.com/"
        268 -> "https://www.xlovecam.com/"
        269 -> "https://profiles.skyprivate.com/"
        270 -> "https://www.coomeet.me/"
        271 -> "https://www.xcams.com/"
        272 -> "https://flirtify.com/"
        273 -> "https://www.sextpanther.com/"
        274 -> "https://sinparty.com/"
        275 -> "https://www.cams.com/"
        276 -> "https://www.sakuralive.com/index.shtml"
        277 -> "https://slutroulette.com/"
        278 -> "https://www.luckycrush.live/"
        279 -> "https://xtease.com/"
        else -> ""
    }
}

internal fun getImageFromServerId(id: Int): String {
    return when (id) {
        1 -> "https://img.icons8.com/color/512/pornhub.png"  // PornHub
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

        30 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "MotherLess"
        31 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "XMoviesForYou"
        32 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "YourPorn"
        33 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "Porn One"
        34 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "3movs"
        35 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "PornDig"
        36 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "CumLouder"
        37 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "TXXX"
        38 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "Porn Doe"
        39 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "PornHat"
        40 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "OK.xxx"
        41 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "Porn00"
        42 -> "PornHoarder"
        43 -> "YesPornVip"
        44 -> "PornHits"
        45 -> "Porn GO"
        46 -> "WhoresHub"
        47 -> "Vsex"
        48 -> "Serviporno"
        49 -> "Poringa"
        50 -> "MuyZorras"
        51 -> "Porno Gratis diario"
        52 -> "Cerdas"
        53 -> "PornoReino"
        54 -> "CanalPorno"
        55 -> "PetardasHD"
        56 -> "LovinSiren"
        57 -> "VideosEgg"
        58 -> "Pulpo69"
        59 -> "Rubias19"
        60 -> "Porn.es"
        61 -> "Feliz Porno"
        62 -> "ConejoX"
        63 -> "Xorgasmo"
        64 -> "PelisXPorno"
        65 -> "Serakon"
        66 -> "PorNudes"
        67 -> "PerformerPedia"
        68 -> "PornHD3x"
        69 -> "PornDish"
        70 -> "Full Porner"
        71 -> "Porn4Days"
        72 -> "ParadiseHills"
        73 -> "PornHd8K"
        74 -> "Perfect Girls"
        75 -> "Your daily porn videos"
        76 -> "EroMe"
        77 -> "Porn300"
        78 -> "AnySex"
        79 -> "VXXX"
        80 -> "VePorn"
        81 -> "DrTuber"
        82 -> "NetFapx"
        83 -> "LetsJerk"
        84 -> "PornoBae"
        85 -> "PornoMz"
        86 -> "XMegaDrive"
        87 -> "Brazzers3x"
        88 -> "Hitprn"
        89 -> "CzechVideo"
        90 -> "WatchXXXFree"
        91 -> "HDPorn92"
        92 -> "YesPornPlease"
        93 -> "Reddit.tube"
        94 -> "FUXNXX"
        95 -> "4KPorn"
        96 -> "InPorn"
        97 -> "PornTop"
        98 -> "Anyporn"
        99 -> "LatestPornVideos"
        100 -> "PhoneErotica"

        // Cartoon and manga
        200 -> "https://ih1.redbubble.net/image.2357425361.6067/raf,360x360,075,t,fafafa:ca443f4786.u2.jpg"  // Hanime.tv
        201 -> "https://www.soviet-power.com/image/cache/data/2021/w165%20%D0%B0-550x550.jpg" // HentaiCloud
        202 -> "https://hentaigasm.tv/wp-content/uploads/2024/12/HG.png" // HentaiGasm
        203 -> "https://hentaimama.tv/wp-content/uploads/2022/08/1-favicon_PNG.png" // HentaiMama
        204 -> "https://hentaimama.tv/wp-content/uploads/2022/08/1-favicon_PNG.png" // HentaiMama
        205 -> "https://hentaimama.tv/wp-content/uploads/2022/08/1-favicon_PNG.png" // HentaiMama
        206 -> "https://ih1.redbubble.net/image.1118561511.1643/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.jpg" // HentaiTube
        207 -> "https://ih1.redbubble.net/image.1118561511.1643/bg,f8f8f8-flat,750x,075,f-pad,750x1000,f8f8f8.jpg" // HentaiTube
        208 -> "https://www.soviet-power.com/image/cache/data/2021/w165%20%D0%B0-550x550.jpg" // HentaiPlay
        209 -> "https://cdn2.steamgriddb.com/logo_thumb/3bbc8b8ac2f0e75cafa24ec9b9530352.png" // MuchoHentai
        210 -> "https://static.wikia.nocookie.net/logopedia/images/1/1c/Machinima.svg/revision/latest/scale-to-width-down/200?cb=20161120075354" // Naughty Machinima
        211 -> "https://pandatools.org/wp-content/uploads/2024/06/image-54.png" // OHentai
        212 -> "https://thumbs.dreamstime.com/b/hentai-rosette-stamp-imitation-grunge-style-designed-round-ribbon-small-crowns-blue-vector-rubber-print-text-texture-136328212.jpg" // PorCore
        213 -> "https://assets.thepornmap.com/wp-content/uploads/20251104195912/xanimeporn.png" // xAnimePorn
        214 -> "https://ei.rdtcdn.com/m=eOhlbe/media/pics/sites/006/590/561/cover1687211108/1687211108.jpg" // AniPorn
        215 -> "https://cdn.displate.com/artwork/380x270/2023-01-11/258b544708e5360b2a577e1311c67a40_ebb0719b47c2dcc503bbd57cb226caa8.jpg" // ZZCartoon
        216 -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQVC6zfQDXZ3GLXR2HebVdgj-n7vAdPB0jxbQ&s" // PornHub
        217 -> "https://preview.redd.it/bi0o1d5qw7y21.jpg?auto=webp&s=55351ad97b3ae9503562da00d2d96d62f858899f" // HentaiHeaven
        218 -> "https://preview.redd.it/bi0o1d5qw7y21.jpg?auto=webp&s=55351ad97b3ae9503562da00d2d96d62f858899f" // HentaiHeaven
        219 -> "https://preview.redd.it/bi0o1d5qw7y21.jpg?auto=webp&s=55351ad97b3ae9503562da00d2d96d62f858899f" // HentaiHeaven

        // LiveCams
        250 -> "https://logowik.com/content/uploads/images/chaturbate1720166505.logowik.com.webp" // Chaturbate
        251 -> "https://www.kamastudioagencia.com/wp-content/uploads/2024/10/amateur-scaled.jpg"  // Amateur.tv
        252 -> "https://juanbustos.com/wp-content/uploads/2019/02/BongaCams_01-copia.jpg"  // BongaCams
        253 -> "https://webcamstartup.com/wp-content/uploads/2024/07/CAM4_Site_Logo.png"  // Cam4
        254 -> "https://play-lh.googleusercontent.com/BByJrJkoUsr1zl4-B16qjyfIlSZxvbiqaga27HCF_EebNkkQfIf2QgX4bXnWhBRMpF4"  // Camsoda
        255 -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTwCS5SYQKu0_tGhtO6d4sJuajxhBtobyCLgw&s" // CamWhoresBay
        256 -> "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQtMQA8Eky7VirE7acAwsscAqsOm3MrxSNvXg&s" // StripChat
        257 -> "https://media.licdn.com/dms/image/v2/C560BAQF0a5MYbE0T2g/company-logo_200_200/company-logo_200_200/0/1630645346067?e=2147483647&v=beta&t=cImXQFmlhNwHsGbfz9Hx80D4jcN9q-BpWm3XKdZ_3Is" // Streamate
        258 -> "https://media.licdn.com/dms/image/v2/C560BAQF0a5MYbE0T2g/company-logo_200_200/company-logo_200_200/0/1630645346067?e=2147483647&v=beta&t=cImXQFmlhNwHsGbfz9Hx80D4jcN9q-BpWm3XKdZ_3Is" // SFW Mock channel
        259 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "Live Jasmin"
        260 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "BabeStation"
        261 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "IMLive"
        262 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "JerkMate"
        263 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "MyFreecams"
        264 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "OleCams"
        265 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "VideoChatErotico"
        266 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "ObsexionCams"
        267 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "FLirtBate"
        268 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "XloveCam"
        269 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "SkyPrivate"
        270 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "Coomeet"
        271 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "XCams"
        272 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "Flirtify"
        273 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "SexPanther"
        274 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "SinParty"
        275 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "Cams.com"
        276 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "SakuraLive"
        277 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "SlutRoullete"
        278 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "LuckyCrush"
        279 -> "https://cdn.staticstack.net/includes/images/logos/theporndude-es.svg" // "Xtease"
        else -> "https://loremflickr.com/400/400/girl?lock=$id"
    }
}