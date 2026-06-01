import express from 'express';
import cors from 'cors'
import { spawn } from 'child_process'

const witubeServer = express()

witubeServer.use(cors())
witubeServer.use(express.json())

witubeServer.get('/', (req, res) => {
    res.send('hola mundo');
});

witubeServer.listen(3000, () => {
    console.log('server listening on port 3000');
})

witubeServer.post('/download-audio', (req, res) => {

    const { url } = req.body;
    const path = req

    const process = spawn('yt-dlp', [
        '-x',
        '--audio-format',
        'mp3',
        '-o',
        '/tmp/audio.%(ext)s',
        url
    ]);

    process.stderr.on('data', data => {
        console.log(data.toString());
    });

    process.on('close', (code) => {

        if (code !== 0) {
            res.status(500).json({ error: 'process convert failed' })
        }

        try {
            res.download('/tmp/audio.mp3');
        } catch (err) {
            res.status(500).json({
                error: "failed download"
            })
        }

    });
}
)

witubeServer.post('/info-audio', (req, res) => {

    const { url } = req.body;

    const process = spawn('yt-dlp', [
        '--dump-json',
        url
    ]);

    let output = '';

    process.stdout.on('data', (data) => {
        output += data.toString();
    });

    process.on('close', (code) => {

        if (code !== 0) {
            return res.status(500).json({
                error: 'failed process yt-dlp'
            });
        }

        try {
            const json = JSON.parse(output);

            res.json(json);
        } catch (err) {
            res.status(500).json({
                error: 'JSON inválido'
            });
        }

    });

})

