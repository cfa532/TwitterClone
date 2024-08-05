(()=>{
    const LIKE_COUNT = "tweet_like_count"

    let tweetid = request["tweetid"]    // tweet id
    let action = request["action"] ? request["action"] : "like"

    var authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, tweetid, "cur")

    let count = lapi.Get(mmsid, LIKE_COUNT)
    if (action == "like") {
        count += 1
    }
    else {
        count -= 1
    }

    lapi.Set(mmsid, LIKE_COUNT, count)
    lapi.MMBackup(authSid, tweetid, "", "delref=true")
    lapi.MiMeiPubish(authSid, "", tweetid)

    return count
})()