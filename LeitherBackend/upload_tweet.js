(() => {
    // let ScorePair = new Function('score', 'member', 'return {score, member}')
    // request, lapi are global variables.
    // each comment is also tweet object.

    const BOOKMARK_COUNT = "tweet_bookmark_count"
    const RETWEET_COUNT = "tweet_retweet_count"
    const COMMENT_COUNT = "tweet_comment_count"
    const LIKE_COUNT = "tweet_like_count"

    const APP_ID = "V6MUd0cVeuCFE7YsGLNn5ygyJlm"
    const APP_EXT = "com.example.twitterclone"
    const APP_MARK = "version 0.0.2"

    // Keys in App mimei database
    const TWT_CONTENT_KEY = "core_data_of_tweet"
    const TWT_LIST_KEY = "list_of_tweets_mid"

    let tweet = JSON.parse(request["tweet"])
    console.log("uploaded ", request["tweet"], request["commentonly"])

    let authSid = lapi.BELoginAsAuthor()
    let mid = lapi.MMCreate(authSid, APP_ID, APP_EXT, tweet.content, 2, 0x07276704)
    tweet["mid"] = mid
    tweet["timestamp"] = Date.now().toString()

    let mmsid = lapi.MMOpen(authSid, mid, "cur")
    lapi.Set(mmsid, TWT_CONTENT_KEY, tweet)
    console.log("tweet=", JSON.stringify(tweet))

    lapi.Set(mmsid, RETWEET_COUNT, 0)
    lapi.Set(mmsid, COMMENT_COUNT, 0)
    lapi.Set(mmsid, LIKE_COUNT, 0)
    lapi.Set(mmsid, BOOKMARK_COUNT, 0)
    lapi.MMBackup(authSid, mid, "", "delref=true")
    lapi.MiMeiPublish(authSid, "", mid)

    if (request["commentonly"] != "true") {
        // only add the tweet in author's tweet list if it is not comment only.
        // otherwise only show the comment under the original tweet
        let appMid = lapi.MMCreate(authSid, APP_ID, APP_EXT, APP_MARK, 2, 0x07276704)
        mmsid = lapi.MMOpen(authSid, appMid, "cur")
        function ScorePair() {}
        sp = new ScorePair
        sp.score = Date.now()
        sp.member = mid
        console.log("appMid=", appMid, sp)
        lapi.Zadd(mmsid, TWT_LIST_KEY, sp)
        lapi.MMBackup(authSid, appMid, "", "delref=true")
        lapi.MMAddRef(authSid, appMid, mid)
        lapi.MiMeiPublish(authSid, "", appMid)
    }
    console.log("new tweet mid=", mid)
    return mid
})()
