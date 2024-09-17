((request, args)=>{
    try {
        const READ_MESSAGE = "read_message_indicator"   // hset of the last time a user message is read.
        const INCOMING_MESSAGE = "incoming_message_indicator"
        const APP_ID = request["aid"]       // App ID assigned by Leither upon publication
        const APP_EXT = "com.example.twitterclone"
        const MESSAGE_MIMEI = "message_mimei"

        // check the last message from anyone who has sent a message.

        let userId = request["userid"]
        let authSid = lapi.BELoginAsAuthor()
        let msgMid = lapi.MMCreate(authSid, APP_ID, APP_EXT, userId+"_"+MESSAGE_MIMEI, 2, 0x07276704)
        let mmsid = lapi.MMOpen("", msgMid, "last")

        // all users who has sent incoming message.
        let senders = lapi.Hkeys(mmsid, INCOMING_MESSAGE)
        console.log("message senders:", senders)

        let messageList = senders.map(senderId => {
            let lastTimeFetched = lapi.Zscore(mmsid, READ_MESSAGE, senderId) || 0;
            let lastMsg = lapi.Hget(mmsid, INCOMING_MESSAGE, senderId)
            if (lastMsg.id /* timestamp of the message */ > lastTimeFetched) {
                lastMsg
            }
        })
        console.log("Incoming from", messageList)
        return messageList  // a list of most recent incoming messages

    } catch(e) {
        console.error(e)
    }
})(request, args)