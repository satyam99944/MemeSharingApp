package com.example.meamshairing

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
//import kotlinx.android.synthetic.main.activity_main.*
class MainActivity : AppCompatActivity() {
   var  nextButton:Button?=null
    var  shareButton:Button?=null
    var memeImageView:ImageView?=null
    var progressBar:ProgressBar?=null
    var currentMemeUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nextButton=findViewById(R.id.nextButton)
        shareButton=findViewById(R.id.shareButton)
        progressBar=findViewById(R.id.progressBar)
        memeImageView=findViewById(R.id.memeImageView)
        loadMeme()
    }
    private fun loadMeme() {
        nextButton?.isEnabled = false
       shareButton?.isEnabled = false
        progressBar?.visibility = View.VISIBLE
        val url = "https://meme-api.herokuapp.com/gimme"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { response ->
                    currentMemeUrl = response.getString("url")

                    this!!.memeImageView?.let {
                        Glide.with(this).load(currentMemeUrl).listener(object : RequestListener<Drawable>{
                            override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                            ): Boolean {
                                progressBar?.visibility = View.GONE
                                nextButton?.isEnabled = true
                                shareButton?.isEnabled = true
                                return false
                            }

                            override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                            ): Boolean {
                                progressBar?.visibility = View.GONE
                                return false
                            }
                        }).into(it)
                    }
                },
                Response.ErrorListener {
                    progressBar?.visibility = View.GONE
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                })

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun showNextMeme(view: View) {
        loadMeme()
    }
    fun shareMeme(view: View){
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_TEXT, "Hi, checkout this meme $currentMemeUrl")
        startActivity(Intent.createChooser(i, "Share this meme with"))
    }
}
