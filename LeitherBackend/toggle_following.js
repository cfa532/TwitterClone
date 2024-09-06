(()=>{
    const FOLLOWINGS_LIST = "list_of_followings_mid"

    let userId = request["userid"]
    let otherId = requet["otherid"]     // user to be followed or unfollowed

    let authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, userId, "cur")

    // check if the otherid is being followed by the user
    let f = lapi.Hget(mmsid, FOLLOWINGS_LIST, otherId)
    if (f) {
        lapi.Hdel(mmsid, FOLLOWINGS_LIST, otherId)
    } else {
        lapi.Hset(mmsid, FOLLOWINGS_LIST, otherId, Date.now())
    }
    lapi.MMBackup(mmsid, userId, "", "delref=true")
    mmsid = lapi.MMOpen(authSid, userId, "last")
    return lapi.Hkeys(mmsid, FOLLOWINGS_LIST)
})()