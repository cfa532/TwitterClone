(()=>{
    const APP_ID = "V6MUd0cVeuCFE7YsGLNn5ygyJlm"
    const APP_EXT = "com.example.twitterclone"
    const APP_MARK = "version 0.0.4"
    const FOLLOWINGS_KEY = "list_of_followings_mid"
    const OWNER_DATA_KEY = "data_of_author"
    const BOOKMARK_COUNT = "tweet_bookmark_count"
    const LIKE_COUNT = "tweet_like_count"
    const COMMENT_COUNT = "tweet_comment_count"
    const FANS_COUNT = "user_followers_count"
    const FOLLOWINGS_COUNT = "user_followings_count"

    // request, lapi are global variables
    let authSid = lapi.BELoginAsAuthor()
    let appMid = lapi.MMCreate(authSid, APP_ID, APP_EXT, APP_MARK, 2, 0x07276704)
    let mmsid = lapi.MMOpen(authSid, appMid, "cur")

    // check if there are data in list of followings. There should be at least the user mid itself
    let followings = lapi.Get(mmsid, FOLLOWINGS_KEY)
    if (!followings) {
        lapi.Set(mmsid, FOLLOWINGS_KEY, [appMid])
        lapi.Set(mmsid, OWNER_DATA_KEY, {mid: appMid})      // create default user data area
        lapi.Set(mmsid, BOOKMARK_COUNT, 0)
        lapi.Set(mmsid, LIKE_COUNT, 0)
        lapi.Set(mmsid, COMMENT_COUNT, 0)
        lapi.Set(mmsid, FANS_COUNT, 0)
        lapi.Set(mmsid, FOLLOWINGS_COUNT, 0)
        lapi.MMBackup(authSid, appMid, "", "delref=true")
        lapi.MiMeiPublish(authSid, "", appMid)
    }
    let user = lapi.RunMApp("get_author_core_data", {aid: request["aid"], ver:"last", userid: appMid})
    console.log(JSON.stringify(user))
    return {sid: authSid, mid: appMid}
    // console.log(appMid)
    // return appMid
})()