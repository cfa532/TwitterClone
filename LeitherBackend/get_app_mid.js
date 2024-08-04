// import { APP_ID, APP_EXT, APP_MARK } from "const.js"

(()=>{
    const APP_ID = "V6MUd0cVeuCFE7YsGLNn5ygyJlm"
    const APP_EXT = "com.example.twitterclone"
    const APP_MARK = "version 0.0.2"
    const FOLLOWINGS_KEY = "list_of_followings_mid"

    // request, lapi are global variables
    let authSid = lapi.BELoginAsAuthor()
    let appMid = lapi.MMCreate(authSid, APP_ID, APP_EXT, APP_MARK, 2, 0x07276704)
    let mmsid = lapi.MMOpen(authSid, appMid, "cur")
    // check if there are data in list of followings. There should be at least the user mid itself
    let followings = lapi.Get(mmsid, FOLLOWINGS_KEY)
    if (!followings) {
        lapi.Set(mmsid, FOLLOWINGS_KEY, [appMid])
        lapi.MMBackup(authSid, appMid, "", "delref=true")
        lapi.MiMeiPublish(authSid, "", appMid)
    }
    return {sid: authSid, mid: appMid}
    // console.log(appMid)
    // return appMid
})()