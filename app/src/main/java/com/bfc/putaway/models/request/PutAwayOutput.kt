import com.google.gson.annotations.SerializedName

data class PutAwayOutput(

    @SerializedName("nxtscr") var nxtscr: String,
    @SerializedName("des01") var des01: String,
    @SerializedName("des02") var des02: String,
    @SerializedName("des03") var des03: String,
    @SerializedName("des04") var des04: String,
    @SerializedName("des05") var des05: String,
    @SerializedName("errmsg") var errmsg: String,
    @SerializedName("spoken") var spoken: String,
    @SerializedName("f1f24flags") var f1f24flags: String,
    @SerializedName("btnflags") var btnflags: String,
    @SerializedName("w1whse") var w1whse: String,
    @SerializedName("w2item") var w2item: String,
    @SerializedName("w3item") var w3item: String,
    @SerializedName("w4item") var w4item: String,
    @SerializedName("w6item") var w6item: String,
    @SerializedName("w8item") var w8item: String,
    @SerializedName("w9aitem") var w9aitem: String,
    @SerializedName("w3odsg") var w3odsg: String,
    @SerializedName("w6dsp") var w6dsp: String,

    @SerializedName("head1") var head1 : String,
    @SerializedName("head2") var head2 : String,
    @SerializedName("head3") var head3 : String,

    @SerializedName("w11slot1q") var w11slot1q: String,
    @SerializedName("w11slot3s") var w11slot3s: String,
    @SerializedName("w11slot2q") var w11slot2q: String,
    @SerializedName("w11slot4s") var w11slot4s: String,
    @SerializedName("w11slot1s") var w11slot1s: String,
    @SerializedName("w11slot2s") var w11slot2s: String,
    @SerializedName("w11slot3q") var w11slot3q: String,
    @SerializedName("w11slot4q") var w11slot4q: String,
    @SerializedName("w11slot2") var w11slot2: String,
    @SerializedName("w11slot1") var w11slot1: String,
    @SerializedName("w11slot4") var w11slot4: String,
    @SerializedName("w11slot3") var w11slot3: String,
    @SerializedName("screen_bc") var screen_bc : String,
    @SerializedName("errmsg_bc") var errmsg_bc : String,
    @SerializedName("errmsg_fc") var errmsg_fc : String


)