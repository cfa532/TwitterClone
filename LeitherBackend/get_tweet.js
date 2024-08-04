(()=>{
    // request, lapi are global variables
    // data necessary to display a tweet item in list
    // const AUTHOR_MID = "tweet_author_id"
    const BOOKMARK_COUNT = "tweet_bookmark_count"
    const RETWEET_COUNT = "tweet_retweet_count"
    const COMMENT_COUNT = "tweet_comment_count"
    const LIKE_COUNT = "tweet_like_count"
    const CONTENT = "content_key"
    const ORIGINAL_TWEET = "original_tweet_mid"
    const ATTACHMENTS = "attachements_key"
    const PRIVACY = "privacy_key"
    const TWT_CONTENT_KEY = "core_data_of_tweet"

    let tweetId = request["tweetid"]
    console.log("tweet mid=", tweetId)
    let mmsid = lapi.MMOpen("", tweetId, "last")
    let author = JSON.parse(lapi.Get(mmsid, TWT_CONTENT_KEY))
    author = author ? author : {}

    let bookmarkCount = lapi.Get(mmsid, BOOKMARK_COUNT)
    let retweetCount = lapi.Get(mmsid, RETWEET_COUNT)
    let commentCount = lapi.Get(mmsid, COMMENT_COUNT)
    let likeCount = lapi.Get(mmsid, LIKE_COUNT)

    return {
        "authorId": author.authorId,
        "content": author.content,
        "attachments": author.attachments,
        "isPrivate": author.isPrivate,
        "original": author.original,
        "bookmarkCount": bookmarkCount,
        "retweetCount": retweetCount,
        "commentCount": commentCount,
        "likeCount": likeCount
    }
})()