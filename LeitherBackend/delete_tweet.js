(()=>{
    const TWT_LIST_KEY = "list_of_tweets_mid"

    let tweetId = request["tweetid"]    // tweet Id to be removed
    let authorMid = request["authorid"]

    const authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, authorMid, "cur")
    lapi.Zrem(mmsid, TWT_LIST_KEY, tweetId)
    lapi.MMDelRef(authSid, authorMid, tweetId)
    lapi.MMBackup(authSid, authorMid, "", "delref=true")

    mmsid = lapi.MMOpen(authSid, tweetId, "cur")
    lapi.MMDelVers(mmsid)
    lapi.MMBackup(authSid, tweetId, "", "delref=true")
    console.log("Delete tweet mid=", tweetId)
    return tweetId
    // lapi.MiMeiPublish(authSid, "", authorMid)
})()