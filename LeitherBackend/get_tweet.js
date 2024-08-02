(()=>{
    // request, lapi are global variables
    // data necessary to display a tweet item in list
    const AUTHOR_MID = "tweet_author_id"
    const BOOKMARK_COUNT = "tweet_bookmark_count"
    const RETWEET_COUNT = "tweet_retweet_count"
    const COMMENT_COUNT = "tweet_comment_count"
    const LIKE_COUNT = "tweet_like_count"
    const CONTENT = "content_key"
    const ORIGINAL_TWEET = "original_tweet_mid"
    const ATTACHMENTS = "attachements_key"
    const PRIVACY = "privacy_key"

    let tweetId = request["tweetid"]
    console.log("tweet mid=", tweetId)
    let mmsid = lapi.MMOpen("", tweetId, "last")
    let authorId = lapi.Get(mmsid, AUTHOR_MID)
    let content = lapi.Get(mmsid, CONTENT)
    let attachments = lapi.Get(mmsid, ATTACHMENTS)
    let privacy = lapi.Get(mmsid, PRIVACY)
    let original = lapi.Get(mmsid, ORIGINAL_TWEET)
    let bookmarkCount = lapi.Get(mmsid, BOOKMARK_COUNT)
    let retweetCount = lapi.Get(mmsid, RETWEET_COUNT)
    let commentCount = lapi.Get(mmsid, COMMENT_COUNT)
    let likeCount = lapi.Get(mmsid, LIKE_COUNT)

    let ret = {
        "authorId": authorId,
        "content": content,
        "attachments": attachments,
        "privacy": privacy,
        "originalId": original,
        "bookmarkCount": bookmarkCount,
        "retweetCount": retweetCount,
        "commentCount": commentCount,
        "likesCount": likeCount
    }

    ret
})()