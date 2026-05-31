import express from 'express';
import cors from 'cors'
import { spawn } from 'child_process'

const witubeServer = express()

witubeServer.use(cors())
witubeServer.use(express.json())

witubeServer.get('/', (req, res) => {
    res.json({
        message: 'servidor funcionando'
    })
})

witubeServer.listen(3000, () => {
    console.log('servidor en el  puerto 3000');
})

// witubeServer.post('/download-audio', async (req, res) => {

//     const { url } = req.body;

//     const process = await spawn('yt-dlp', [
//         '-x',
//         '--audio-format',
//         'mp3',
//         '-o',
//         '/tmp/audio.%(ext)s',
//         url
//     ]);

//     process.stderr.on('data', data => {
//         console.log(data.toString());
//     });

//     process.on('close', (code) => {
//         console.log('Código:', code);

//         await res.download('/tmp/audio.mp3');
//     });
// }
// )

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

        if (req.ok) {
            return res.status(500).json({
                error: 'yt-dlp falló'
            });
        }

        try {
            const json = JSON.parse(output);
            res.json(json);
        } catch (error) {
            res.status(500).json({
                error: 'JSON inválido'
            });
        }

    });

})

