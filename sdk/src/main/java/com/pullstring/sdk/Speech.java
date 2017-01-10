/*
 * Encapsulate a conversation thread for PullString's Web API.
 *
 * Copyright (c) 2016 PullString, Inc.
 *
 * The following source code is licensed under the MIT license.
 * See the LICENSE file, or https://opensource.org/licenses/MIT.
 */
package com.pullstring.sdk;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Facilitate collection and processing of audio data for ASR.
 */
class Speech {

    /**
     * Start allowing audio to be streamed to the server.
     */
    void start() {
        mIsRecording = true;
    }

    /**
     * Stop allowing audio to be streamed to the server.
     */
    void stop() {
        mIsRecording = false;
    }

    /**
     * Send an audio chunk to the server
     */
    void streamAudio(short[] samples, OutputStream stream) {
        if (!mIsRecording || samples == null || samples.length == 0 || stream == null) {
            return;
        }

        byte[] data = samplesToBytes(samples);

        try {
            stream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Take the raw bytes of a .wav file, validate the header, and retrieve the raw audio bytes.
     * Since we don't know the size of the buffer ahead of time, return a byte array and pass in
     * a StringBuilder to populate with any errors.
     */
    static byte[] getWavData(byte[] inData, StringBuilder err) {
        ByteBuffer bb = ByteBuffer.wrap(inData);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        // Check that it describes itself as a .wav file
        String riff = new String(inData, 0, 4);
        if (!riff.equals("RIFF")){
            err.append("Data is not a WAV file");
            return null;
        }

        // Enforce 16-bit mono at 16000 sample rate
        short channels = bb.getShort(22);
        int rate = bb.getInt(24);
        short bitsPerSample = bb.getShort(34);

        if (channels != 1 || rate != 16000 || bitsPerSample != 16) {
            err.append("WAV data is not mono 16-bit data at 16k sample rate");
            return null;
        }

        // Find the actual samples
        int dataOffset = 12;
        int fileSize = bb.getInt(4);
        int chunkSize = bb.getInt(16);

        while (!(new String(inData, dataOffset, 4).equals("data"))) {
            if (dataOffset > fileSize) {
                err.append("Cannot find data segment in WAV file");
                return null;
            }

            dataOffset += chunkSize + 8;
            chunkSize = bb.getInt(dataOffset + 4);
        }

        // Get the samples
        int dataStart = dataOffset + 8;
        int dataSize = inData.length - dataStart;
        byte[] outData = new byte[dataSize];

        bb.position(dataStart);
        bb.get(outData);

        return outData;
    }

    /**
     * Take a buffer of samples and convert to little endian bytes.
     */
    private static byte[] samplesToBytes(short[] samples) {

        byte[] data = new byte[samples.length * BYTES_PER_SAMPLE];

        for (int i = 0; i < samples.length; i++) {
            data[i * 2] = (byte)(samples[i] & 0x00FF);
            data[i * 2 + 1] = (byte)(samples[i] >> 8);
            samples[i] = 0;
        }

        return data;
    }

    private boolean mIsRecording;
    private final static int BYTES_PER_SAMPLE = 2;
}
