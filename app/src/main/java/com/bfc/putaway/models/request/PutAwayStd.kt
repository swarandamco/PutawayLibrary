import com.google.gson.annotations.SerializedName

data class PutAwayStd(
	@SerializedName("app") var app: String,
	@SerializedName("user") var user: String,
	@SerializedName("uid") var uid: String,
	@SerializedName("whereFrom") var whereFrom: String,
	@SerializedName("whouse") var whouse: String
)