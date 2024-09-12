((request, args) => {
    try {
        const MESSAGE_MIMEI = "message_mimei"
        const APP_ID = request["aid"]       // App ID assigned by Leither upon publication
        const APP_EXT = "com.example.twitterclone"

        let receiptId = request["receipt"]
        let userId = request["sender"]
        let authSid = lapi.BELoginAsAuthor()

        // create a Mimei for all messages, incoming and outgoing.
        let msg = JSON.parse(request["msg"])
        let msgMid = lapi.MMCreate(authSid, APP_ID, APP_EXT, userId+"_"+MESSAGE_MIMEI, 2, 0x07276704)
        let mmsid = lapi.MMOpen(authSid, msgMid, "cur")

        function ScorePair() {}
        sp = new ScorePair
        sp.score = Date.now()
        sp.member = msg.id

        // use a zset as message index and hset to store message.
        // senderId is the key for both.
        lapi.Zadd(mmsid, receiptId, sp)
        lapi.Hset(mmsid, receiptId, msg.id, msg)
        lapi.MMBackup(authSid, msgMid, "", "delref=true")

    } catch(e) {
        console.error(e)
    }
})(request, args)