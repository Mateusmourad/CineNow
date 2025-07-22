package com.devspacecinenow

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.devspacecinenow.ui.theme.CineNowTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CineNowTheme {

                var nowPlayingMovies by remember { mutableStateOf<List<MovieDto>> (emptyList()) }

                val apiService = RetrofitClient.retrofitInstance.create(ApiService::class.java)
                val callNowPlaying = apiService.getNowPlayingMovies()

                callNowPlaying.enqueue(object : Callback<MovieResponse> {
                    override fun onResponse(
                        call: Call<MovieResponse>,
                        response: Response<MovieResponse>
                    ) {
                        if (response.isSuccessful) {
                            val movies = response.body()?.results
                            if(movies != null) {
                                nowPlayingMovies = movies
                            }

                        } else {
                            Log.d("MainActivity", "Request Error :: ${response.errorBody()}")
                        }
                    }

                    override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                        Log.d("MainActiviy", "Network Error :: ${t.message}")
                    }

                }
                )
                

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MovieList(nowPlayingMovies){
                    movieClicked->
                    }



                    LazyColumn {
                        items(nowPlayingMovies){
                            Text(text = it.title)
                        }
                    }
                }
            }
        }
    }
    @Composable
    fun MovieList (
        movieList: List<MovieDto>,
        onClick: () -> Unit
    ) {
        LazyRow {
            items(movieList) {
                MovieItem(movieDto = it,
                    onClick = onClick)

                }
            }
        }

}

    @Composable
    fun MovieItem(
        movieDto: MovieDto,
        onClick: () -> Unit
    ) {
        Column (
            modifier = Modifier.clickable {
                onClick.invoke()
            }
        ){
            AsyncImage(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .width(120.dp)
                    .height(150.dp),
                        contentScale = ContentScale.Crop,

                model = movieDto.posterFullPath,
                contentDescription = "${movieDto.title} Poster image")
        }
    }

