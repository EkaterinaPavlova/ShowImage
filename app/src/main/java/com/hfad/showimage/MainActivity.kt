package com.hfad.showimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hfad.showimage.databinding.ActivityMainBinding
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.urlImageGlide.setOnClickListener {
            val imageUrl = binding.urlImageGlide.text.toString()
            showImageGlide(imageUrl)
        }

        handler = Handler(Looper.getMainLooper())
        binding.urlImageStandart.setOnClickListener {
            val imageUrl = binding.urlImageStandart.text.toString()
            showImageStandart(imageUrl)
        }
    }

    private fun showImageStandart(Url: String) {
        Thread {
            try {
                val imageUrl = URL(Url)
                val connection = imageUrl.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.let {
                    handler.post { setImage(bitmap) }
                }

            } catch (error: Exception) {
                handler.post {
                    Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun setImage(bitmap: Bitmap) {
        binding.image.setImageBitmap(bitmap)
    }

    private fun showImageGlide(Url: String) {
        Glide.with(this)
            .load(Uri.parse(Url))
            .error(R.drawable.ic_baseline_error_24)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Toast.makeText(this@MainActivity, "Error: $e", Toast.LENGTH_SHORT).show()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .into(binding.imageGlide)
    }
}