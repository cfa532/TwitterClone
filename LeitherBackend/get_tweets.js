((request, args)=>{
    try {
        const TWT_LIST_KEY = "list_of_tweets_mid"

        let startScore = request["start"]
        let endScore = request["end"] ? request["end"] : -1
        let userId = request["userid"]
        let mmsid = lapi.MMOpen("", userId, "last")
    
        let arr = lapi.Zrevrangebyscore(mmsid, TWT_LIST_KEY, startScore, endScore)
        let ret = arr.map(sp => {
            return lapi.RunMApp("get_tweet", {aid: request["aid"], ver:"last", userid: userId, tweetid: sp.Member}, [])
        })
        return JSON.stringify(ret)
    } catch(e) {
        return e
    }
})(request, args)