(()=>{
    const OWNER_DATA_KEY = "data_of_author"
    
    // request, lapi are global variables
    console.log("Author mid=", request["user"])
    let author = JSON.parse(request["user"])
    const authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, author.mid, "cur")
    lapi.Set(mmsid, OWNER_DATA_KEY, author)
    lapi.MMBackup(authSid, author.mid, "", "delref=true")
    lapi.MiMeiPublish(authSid, "", author.mid)
})()