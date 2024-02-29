const express = require('express');
const qrcode = require('qrcode');
const { Client } = require('whatsapp-web.js');

const app = express();
const port = 3000;

const client = new Client();
let isClientReady = false;
let qrCodeDataUrl; // Variable to store QR code data URL

app.get('/qrcode', (req, res) => {
    // Check if the QR code data URL is available
    if (qrCodeDataUrl) {
        // Set the response type to image/png
        res.type('image/png');
        // Send the QR code image as a binary response
        res.send(Buffer.from(qrCodeDataUrl.split('base64,')[1], 'base64'));
    } else {
        res.status(404).send('QR code not available yet. Please try again later.');
    }
});

app.get('/status', (req, res) => {
    res.send(`Client is ${isClientReady ? 'ready' : 'not ready'}`);
});

app.get('/send-message', (req, res) => {
    const { number, message } = req.query;

    if (!number || !message) {
        return res.status(400).send('Invalid request. Both number and message are required.');
    }

    // Assuming the number is a Lebanese number, you might want to format it accordingly
    const formattedNumber = `961${number}@c.us`;

    if (!isClientReady) {
        return res.status(500).send('Client is not ready yet.');
    }

    // Send the message
    client.sendMessage(formattedNumber, message).then(() => {
        res.send('Message sent successfully!');
    }).catch((error) => {
        console.error('Error sending message:', error);
        res.status(500).send('Error sending message.');
    });
});

client.on('qr', async (qr) => {
    try {
        // Generate QR code as a data URL
        qrCodeDataUrl = await qrcode.toDataURL(qr);
    } catch (error) {
        console.error('Error generating QR code image:', error);
    }
});

client.on('ready', () => {
    // Handle ready event (optional)
    console.log('Client is ready!');
    isClientReady = true;
});

// Start the session
client.initialize();

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
