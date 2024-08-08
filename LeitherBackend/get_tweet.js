(()=>{
    // request, lapi are global variables
    // data necessary to display a tweet item in list
    // const AUTHOR_MID = "tweet_author_id"
    const BOOKMARK_COUNT = "tweet_bookmark_count"
    const RETWEET_COUNT = "tweet_retweet_count"
    const COMMENT_COUNT = "tweet_comment_count"
    const LIKE_COUNT = "tweet_like_count"
    const TWT_CONTENT_KEY = "core_data_of_tweet"
    const LIKE_LIST = "tweet_like_list"
    const BOOKMARK_LIST = "tweet_bookmark_list"

    let tweetId = request["tweetid"]
    let userId = request["userid"]
    console.log("tweet mid=", tweetId)
    let mmsid = lapi.MMOpen("", tweetId, "last")
    let tweet = lapi.Get(mmsid, TWT_CONTENT_KEY)

    let bookmarkCount = lapi.Get(mmsid, BOOKMARK_COUNT)
    let retweetCount = lapi.Get(mmsid, RETWEET_COUNT)
    let commentCount = lapi.Get(mmsid, COMMENT_COUNT)
    let likeCount = lapi.Get(mmsid, LIKE_COUNT)

    // check if user has bookmarked or liked the tweet
    let hasLiked = lapi.Hget(mmsid, LIKE_LIST, userId)
    let hasBookmarked = lapi.Hget(mmsid, BOOKMARK_LIST, userId)

    return {
        // tweet core data
        "mid": tweet.mid,
        "authorId": tweet.authorId,
        "content": tweet.content,
        "attachments": tweet.attachments,
        "isPrivate": tweet.isPrivate,
        "original": tweet.original,

        "bookmarkCount": bookmarkCount,
        "retweetCount": retweetCount,
        "commentCount": commentCount,
        "likeCount": likeCount,

        "hasLiked": hasLiked ? true : false,
        "hasBookmarked": hasBookmarked ? true : false
    }
})()