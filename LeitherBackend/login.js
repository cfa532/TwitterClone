((request, args)=>{
    try {
        const APP_ID = request["aid"]       // App ID assigned by Leither upon publication
        const APP_EXT = "com.example.twitterclone"

        let userId = lapi.MMCreate(authSid, APP_ID, APP_EXT, request["phrase"], 2, 0x07276704)
        let user = lapi.MMOpen("", userId, "last")
        // need to check hashed password
        if (user.username == request["username"] && user.password == request["password"])
            return user
        else
            return {}
    } catch(e) {
        return e
    }
})(request, args)