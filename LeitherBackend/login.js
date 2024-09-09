((request, args)=>{
    try {
        const APP_ID = request["aid"]       // App ID assigned by Leither upon publication
        const APP_EXT = "com.example.twitterclone"
        const OWNER_DATA_KEY = "data_of_author"

        let authSid = lapi.BELoginAsAuthor()
        let userId = lapi.MMCreate(authSid, APP_ID, APP_EXT, request["phrase"], 2, 0x07276704)
        let mmsid = lapi.MMOpen("", userId, "last")
        let user = lapi.Get(mmsid, OWNER_DATA_KEY)
        // need to check hashed password
        if (user.username == request["username"] && user.password == request["password"]) {
            delete user.password
            console.log("Login()", JSON.stringify(user))
            return JSON.stringify(user)
        }
    } catch(e) {
        return e
    }
})(request, args)