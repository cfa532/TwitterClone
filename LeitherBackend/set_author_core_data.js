(()=>{
    const OWNER_DATA_KEY = "data_of_author"
    
    // request, lapi are global variables
    let author = JSON.parse(request["author"])
    console.log("Author mid=", request["author"])
    const authSid = lapi.BELoginAsAuthor()
    let mmsid = lapi.MMOpen(authSid, author.mid, "cur")
    lapi.Set(mmsid, OWNER_DATA_KEY, author)
    lapi.MMBackup(authSid, author.mid, "", "delref=true")
    lapi.MiMeiPublish(authSid, "", author.mid)
})()