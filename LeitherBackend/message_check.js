((request, args)=>{
    try {
        const READ_MESSAGE = "read_message_indicator"   // hset of the last time a user message is read.
        const INCOMING_MESSAGE = "incoming_message_indicator"
        const APP_ID = request["aid"]       // App ID assigned by Leither upon publication
        const APP_EXT = "com.example.twitterclone"
        const MESSAGE_MIMEI = "message_mimei"

        let userId = request["userid"]
        // lapi is a global handle to a Redis database.
        let msgMid = lapi.MMCreate(authSid, APP_ID, APP_EXT, userId+"_"+MESSAGE_MIMEI, 2, 0x07276704)
        let mmsid = lapi.MMOpen("", msgMid, "last")
        let senders = lapi.ZRange(mmsid, INCOMING_MESSAGE, 0, -1)
        console.log("message senders:", senders)
        let members = senders.map(e => {
            // INCOMING_MESSAGE is key of another zset. Give the same e.member, get the score of
            // its correspoing score in that zset. If null, set the score to zero.
            // compare the score of 2nd zset with e.score, if e.score is larger, return e.member
            let readScore = lapi.ZScore(mmsid, READ_MESSAGE, e.member) || 0;
            if (e.score > readScore) {
                return e.member;    // there is new message.
            }
        })
        console.log("Incoming from", members)
        return members  // a list of users who send incoming messages

    } catch(e) {
        console.error(e)
    }
})(request, args)