((request, args)=>{
    try {
        const COMMENT_LIST = "comment_list_key"
        const TWT_CONTENT_KEY = "core_data_of_tweet"
    
        let tweetId = request["tweetid"]
        let mmsid = lapi.MMOpen("", tweetId, "last")
        console.log("tweetId=", tweetId, mmsid)
    
        let arr = lapi.Zrevrange(mmsid, COMMENT_LIST, 0, -1)
        console.log(arr.length)
        console.log("arr[0]=",arr[0])
        return arr.map(sp => {
            console.log("sp=", JSON.stringify(sp))
            let csid = lapi.MMOpen("", sp.member, "last")
            let tweet = lapi.Get(csid, TWT_CONTENT_KEY)
            return tweet
        })
    } catch(e) {
        return e
    }
    })(request, args)