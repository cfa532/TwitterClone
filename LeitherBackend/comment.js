(()=>{
    // request, lapi are global variables
    // each comment is a tweet object
    const COMMENT_COUNT = "comment_count_key"
    const COMMENT_LIST = "comment_list_key"

    let tweetId = request["tweetid"]
    console.log("tweet mid=", tweetId)

    var authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, tweetId, "cur")
    let count = lapi.Get(mmsid, COMMENT_COUNT)
    lapi.Set(mmsid, COMMENT_COUNT, count)

    let cl = lapi.Get(mmsid, COMMENT_LIST)
    if (request["action"] != "remove") {
        count++
        lapi.Set(mmsid, COMMENT_LIST, cl.push(tweetId))
    } else {
        // remove the tweet ID
        count--
        cl = cl.filter(item => item !== tweetId)
        lapi.Set(mmsid, COMMENT_LIST, cl)
    }
    lapi.MMBackup(sid, tweetId, "", "delref=true")
    lapi.MiMeiPubish(authSid, "", tweetid)

    count
})()