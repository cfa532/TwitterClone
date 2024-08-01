(()=>{
    // request, lapi are global variables
    // each comment is a tweet object
    const AUTHOR_MID = "tweet_author_id"
    const BOOKMARK_COUNT = "tweet_bookmark_count"
    const RETWEET_COUNT = "tweet_retweet_count"
    const COMMENT_COUNT = "tweet_comment_count"
    const LIKE_COUNT = "tweet_like_count"
    const CONTENT = "content_key"
    const ORIGINAL_TWEET = "original_tweet_mid"
    const ATTACHMENTS = "attachements_key"
    const PRIVACY = "privacy_key"

    let tweet = JSON.parse(request["tweet"])
    console.log("tweet=", tweet)

    var authSid = lapi.BELoginAsAuthor()
    let mid = MMCreate(authSid, "V6MUd0cVeuCFE7YsGLNn5ygyJlm", "com.example.twitterclone", "{{auto}}", 2, 0x07276704)
    let mmsid = lapi.MMOpen(authSid, mid, "cur")
    lapi.Set(mmsid, AUTHOR_MID, tweet.authorId)
    lapi.Set(mmsid, "mid", mid)
    lapi.Set(mmsid, "timestamp", Date.now())
    lapi.Set(mmsid, CONTENT, tweet.content)
    lapi.Set(mmsid, ORIGINAL_TWEET, tweet.original)
    lapi.Set(mmsid, ATTACHMENTS, tweet.attachments)
    lapi.Set(mmsid, PRIVACY, tweet.isPrivate)

    lapi.Set(mmsid, RETWEET_COUNT, 0)
    lapi.Set(mmsid, COMMENT_COUNT, 0)
    lapi.Set(mmsid, LIKE_COUNT, 0)
    lapi.Set(mmsid, BOOKMARK_COUNT, 0)
    lapi.MMBackup(sid, mid, "", "delref=true")
    lapi.MiMeiPubish(authSid, "", mid)

    mid
})()