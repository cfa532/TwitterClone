(()=>{
    const APP_ID = "V6MUd0cVeuCFE7YsGLNn5ygyJlm"
    const APP_EXT = "com.example.twitterclone"
    const APP_MARK = "version 0.0.2"
    const FOLLOWINGS_KEY = "list_of_followings_mid"
    const OWNER_DATA_KEY = "data_of_author"

    // request, lapi are global variables
    let authSid = lapi.BELoginAsAuthor()
    let appMid = lapi.MMCreate(authSid, APP_ID, APP_EXT, APP_MARK, 2, 0x07276704)
    let mmsid = lapi.MMOpen(authSid, appMid, "cur")

    // check if there are data in list of followings. There should be at least the user mid itself
    let followings = lapi.Get(mmsid, FOLLOWINGS_KEY)
    if (!followings) {
        lapi.Set(mmsid, FOLLOWINGS_KEY, [appMid])
        lapi.Set(mmsid, OWNER_DATA_KEY, {mid: appMid})
        lapi.MMBackup(authSid, appMid, "", "delref=true")
        lapi.MiMeiPublish(authSid, "", appMid)
    }
    let user = lapi.RunMApp("get_author_core_data", {aid: request["aid"], ver:"last", userid: appMid})
    console.log(JSON.stringify(user))
    return {sid: authSid, mid: appMid}
    // console.log(appMid)
    // return appMid
})()