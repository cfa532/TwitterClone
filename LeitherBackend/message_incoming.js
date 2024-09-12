((request, args) => {
    try {
        const INCOMING_MESSAGE = "incoming_message_indicator"
        const MESSAGE_MIMEI = "message_mimei"
        const APP_ID = request["aid"]       // App ID assigned by Leither upon publication
        const APP_EXT = "com.example.twitterclone"

        let senderId = request["sender"]
        let userId = request["receipt"]  
        let authSid = lapi.BELoginAsAuthor()
        
        // create a Mimei for all messages, incoming and outgoing.
        let msgMid = lapi.MMCreate(authSid, APP_ID, APP_EXT, userId+"_"+MESSAGE_MIMEI, 2, 0x07276704)
        let mmsid = lapi.MMOpen(authSid, msgMid, "cur")

        // within user MM, create a zset to store all users who has ever send a message.
        function ScorePair() {}
        sp = new ScorePair
        sp.score = Date.now()
        sp.member = senderId
        // score will be updated if the same memeber exists.
        lapi.Zadd(mmsid, INCOMING_MESSAGE, sp)
        lapi.MMBackup(authSid, userId, "", "delref=true")

        // use a zset as message index and hset to store message.
        // senderId is the key for both.
        let msg = JSON.parse(request["msg"])
        sp.member = msg.id
        lapi.Zadd(mmsid, senderId, sp)
        lapi.Hset(mmsid, senderId, msg.id, msg)
        lapi.MMBackup(authSid, msgMid, "", "delref=true")

    } catch(e) {
        console.error(e)
    }
})(request, args)