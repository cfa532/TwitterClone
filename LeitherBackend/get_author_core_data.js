(()=>{
    const OWNER_DATA_KEY = "data_of_author"

    // request, lapi are global variables
    let authorId = request["authorid"]
    let mmsid = lapi.MMOpen("", authorId, "last")

    // return a few attributes for preview
    return lapi.Get(mmsid, OWNER_DATA_KEY)
})()