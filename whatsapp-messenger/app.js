const express = require('express');
const qrcode = require('qrcode');
const { Client } = require('whatsapp-web.js');

const app = express();
const port = 3000;

const client = new Client();
let isClientReady = false;

app.get('/qrcode', (req, res) => {
    // Listen for the 'qr' event to receive the QR code string
    client.on('qr', async (qr) => {
        try {
            // Generate QR code as a data URL
            const qrImage = await qrcode.toDataURL(qr);

            // Set the response type to image/png
            res.type('image/png');

            // Send the QR code image as a binary response
            res.send(Buffer.from(qrImage.split('base64,')[1], 'base64'));
        } catch (error) {
            console.error('Error generating QR code image:', error);
            res.status(500).send('Internal Server Error');
        }
    });

    // Start the session
    client.initialize();
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



client.on('ready', () => {
    // Handle ready event (optional)
    console.log('Client is ready!');
    isClientReady = true;
});

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
