(()=>{
    let tweetid = request["tid"]    // tweet id
    let action = request["action"] ? request["action"] : "like"

    var authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, tweetid, "cur")

    let count = lapi.Get(mmsid, "count_of_likes")
    if (action == "like") {
        count += 1
    }
    else {
        count -= 1
    }

    lapi.Set(mmsid, "count_of_likes", count)
    lapi.MMBackup(authSid, tweetid, "", "delref=true")
    lapi.MiMeiPubish(authSid, "", tweetid)

    count
})()