(()=>{
    // request, lapi are global variables
    // data necessary to display a tweet item in list
    const AUTHOR_MID = "tweet_author_id"
    const BOOKMARK_COUNT = "tweet_bookmark_count"
    const RETWEET_COUNT = "tweet_retweet_count"
    const COMMENT_COUNT = "tweet_comment_count"
    const LIKE_COUNT = "tweet_like_count"
    const CONTENT_KEY = "content_key"
    const ORIGINAL_TWEET = "original_tweet_mid"
    const ATTACHMENTS_KEY = "attachements_key"
    const PRIVACY_KEY = "privacy_key"

    let tweetId = request["tweetid"]
    console.log("tweet mid=", tweetId)
    let mmsid = lapi.MMOpen("", tweetId, "last")

    // get full author data and return a few attributes for preview
    JSON.parse(lapi.Get(mmsid, "data_of_node_owner"))
})()