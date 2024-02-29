package com.example.myapplication

import android.R
import android.app.Activity
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.theme.MyApplicationTheme
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException
import javax.microedition.khronos.opengles.GL10


import javax.microedition.khronos.egl.EGLConfig
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin
//import instructions for sphere rendering




class MainActivity : ComponentActivity() {

    private lateinit var glSurfaceView: GLSurfaceView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        glSurfaceView = findViewById<GLSurfaceView>(R.id.glSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(MyRenderer())
    }
}


class MyRenderer : GLSurfaceView.Renderer {

    // Vertex and fragment shader code
    private val vertexShaderCode =
        """
        attribute vec4 vPosition;
        void main() {
            gl_Position = vPosition;
        }
        """.trimIndent()

    private val fragmentShaderCode =
        """
        precision mediump float;
        uniform vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
        """.trimIndent()

    private var mProgram: Int = 0
    private lateinit var vertexBuffer: FloatBuffer

    // Number of coordinates per vertex in this array
    private val COORDS_PER_VERTEX = 3
    private val sphereVertices: FloatArray
    private val vertexCount: Int

    // Set color with red, green, blue and alpha (opacity) values
    private val color = floatArrayOf(0.63671875f, 0.76953125f, 0.22265625f, 1.0f) // Yellow

    init {
        val radius = 1.0f
        val slices = 20
        val stacks = 20

        val vertices = mutableListOf<Float>()

        for (i in 0..stacks) {
            val theta1 = (i * Math.PI / stacks).toFloat()
            val theta2 = ((i + 1) * Math.PI / stacks).toFloat()

            for (j in 0..slices) {
                val phi = (j * 2 * Math.PI / slices).toFloat()

                // Calculate the vertices on the sphere using spherical coordinates
                val x1 = radius * sin(theta1) * cos(phi)
                val y1 = radius * sin(theta1) * sin(phi)
                val z1 = radius * cos(theta1)

                val x2 = radius * sin(theta2) * cos(phi)
                val y2 = radius * sin(theta2) * sin(phi)
                val z2 = radius * cos(theta2)

                vertices.addAll(listOf(x1, y1, z1))
                vertices.addAll(listOf(x2, y2, z2))
            }
        }

        sphereVertices = vertices.toFloatArray()
        vertexCount = sphereVertices.size / COORDS_PER_VERTEX

        // Initialize vertex byte buffer for shape coordinates
        val bb = ByteBuffer.allocateDirect(
            // (# of coordinate values * 4 bytes per float)
            sphereVertices.size * 4
        )

        // Use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder())

        // Create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer()

        // Add the coordinates to the FloatBuffer
        vertexBuffer.put(sphereVertices)
        // Set the buffer to read the first coordinate
        vertexBuffer.position(0)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // Load shaders, compile and link program
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram)

        // Get handle to vertex shader's vPosition member
        val positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition")

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle)

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
            positionHandle, COORDS_PER_VERTEX,
            GLES20.GL_FLOAT, false,
            0, vertexBuffer
        )

        // Get handle to fragment shader's vColor member
        val colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor")

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0)

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount)

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }


    override fun onDrawFrame(gl: GL10) {
        // Render frame
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        // Adjust the viewport when the surface size changes
        GLES20.glViewport(0, 0, width, height)
    }
}





