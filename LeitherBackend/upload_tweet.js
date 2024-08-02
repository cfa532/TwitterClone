(() => {
    // request, lapi are global variables.
    // each comment is also tweet object.
    const AUTHOR_MID = "tweet_author_id"
    const BOOKMARK_COUNT = "tweet_bookmark_count"
    const RETWEET_COUNT = "tweet_retweet_count"
    const COMMENT_COUNT = "tweet_comment_count"
    const COMMENT_LIST = "comment_list_key"
    const LIKE_COUNT = "tweet_like_count"
    const CONTENT = "content_key"
    const ORIGINAL_TWEET = "original_tweet_mid"
    const ATTACHMENTS = "attachements_key"
    const PRIVACY = "privacy_key"

    const APP_ID = "V6MUd0cVeuCFE7YsGLNn5ygyJlm"
    const APP_EXT = "com.example.twitterclone"
    const APP_MARK = "version 0.0.2"

    // Keys in App mimei database
    const TWT_LIST_KEY = "list_of_tweets_mid"

    let tweet = JSON.parse(request["tweet"])
    console.log("tweet=", tweet)

    const authSid = lapi.BELoginAsAuthor()
    let mid = MMCreate(authSid, APP_ID, APP_EXT, tweet.content, 2, 0x07276704)
    // let mid = MMCreate(authSid, "V6MUd0cVeuCFE7YsGLNn5ygyJlm", "com.example.twitterclone", "{{auto}}", 2, 0x07276704)
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
    // lapi.Set(mmsid, COMMENT_LIST, [])
    lapi.Set(mmsid, LIKE_COUNT, 0)
    lapi.Set(mmsid, BOOKMARK_COUNT, 0)
    lapi.MMBackup(authSid, mid, "", "delref=true")
    lapi.MiMeiPublish(authSid, "", mid)

    if (request["commentonly"] != "true") {
        // only add the tweet in author's tweet list if it is not comment only.
        // otherwise only show the comment under the original tweet
        const appMid = MMCreate(authSid, APP_ID, APP_EXT, APP_MARK, 2, 0x07276704)
        mmsid = lapi.MMOpen(authSid, appMid, "cur")
        lapi.Zadd(mmsid, TWT_LIST_KEY, new ScorePair(Date.now(), mid))
        lapi.MMBackup(authSid, appMid, "", "delref=true")
        lapi.MMAddRef(authSid, appMid, mid)
        lapi.MiMeiPublish(authSid, "", appMid)
    }

    mid
})()

class ScorePair {
    constructor(score, member) {
        this.score = score;
        this.member = member;
    }
};