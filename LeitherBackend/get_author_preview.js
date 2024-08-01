(()=>{
    // request, lapi are global variables
    let authorId = request["authorid"]
    console.log("Author mid=", authorId)
    let mmsid = lapi.MMOpen("", authorId, "last")

    // get full author data and return a few attributes for preview
    let author = JSON.parse(lapi.Get(mmsid, "data_of_node_owner"))

    let ret = {name: author.name, username: author.username, avatar: author.avatar}
    ret
})()