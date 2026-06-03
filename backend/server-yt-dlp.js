import express from 'express';
import cors from 'cors';
import { spawn } from 'child_process';
import crypto from 'crypto';
import fs from 'fs';

const witubeServer = express();
const PORT = process.env.PORT || 3000;

witubeServer.use(cors());
witubeServer.use(express.json());

const isValidUrl = (string) => {
    try {
        new URL(string);
        return true;
    } catch (_) {
        return false;
    }
};

const cleanTempFiles = (id) => {
    const tempPattern = `/tmp/${id}`;
    fs.readdir('/tmp', (err, files) => {
        if (err) return;
        files.forEach(file => {
            if (file.startsWith(id)) {
                fs.unlink(`/tmp/${file}`, () => {});
            }
        });
    });
};

witubeServer.get('/', (req, res) => {
    res.send('hola mundo (Witube API activa)');
});


witubeServer.get('/info-audio', (req, res) => {
    res.send('info.json');
});

witubeServer.post('/download-audio', (req, res) => {
    const { url } = req.body;
    if (!url || !isValidUrl(url)) {
        return res.status(400).json({ error: 'A valid URL is required' });
    }

    const id = crypto.randomUUID();
    const outputTemplate = `/tmp/${id}.%(ext)s`;
    const outputFile = `/tmp/${id}.mp3`;

    const downloadProcess = spawn('yt-dlp', [
        '-x',
        '--audio-format',
        'mp3',
        '-o',
        outputTemplate,
        url
    ]);

    let processFinished = false;
    let stderrOutput = '';

    // req.on('close', () => {
    //     if (!processFinished) {
    //         console.log(`[Abort] Descarga cancelada por el cliente. Matando proceso de yt-dlp.`);
    //         downloadProcess.kill('SIGKILL');
    //         cleanTempFiles(id);
    //     }
    // });

    downloadProcess.stderr.on('data', data => {
        stderrOutput += data.toString();
        console.log(`[yt-dlp stderr]: ${data.toString()}`);
    });

    downloadProcess.on('error', (err) => {
        processFinished = true;
        console.error('[Error de ejecución]:', err);
        cleanTempFiles(id);
        if (!res.headersSent) {
            return res.status(500).json({ 
                error: 'Failed to start yt-dlp execution',
                details: err.message 
            });
        }
    });

    downloadProcess.on('close', (code) => {
        processFinished = true;

        if (code !== 0) {
            console.error(`[Error] yt-dlp finalizó con código: ${code}`);
            cleanTempFiles(id);
            if (!res.headersSent) {
                return res.status(500).json({ 
                    error: 'Process convert failed', 
                    details: stderrOutput.trim() 
                });
            }
            return;
        }

        if (!fs.existsSync(outputFile)) {
            console.error(`[Error] El archivo de salida no fue encontrado: ${outputFile}`);
            if (!res.headersSent) {
                return res.status(500).json({ error: 'Converted audio file not found' });
            }
            return;
        }

        res.download(outputFile, 'audio.mp3', (err) => {
            cleanTempFiles(id);

            if (err && !res.headersSent) {
                console.error('[Error al enviar descarga]:', err);
                return res.status(500).json({ error: 'Failed downloading file' });
            }
        });
    });
});

witubeServer.post('/info-audio', (req, res) => {
    const { url } = req.body;
    if (!url || !isValidUrl(url)) {
        return res.status(400).json({ error: 'A valid URL is required' });
    }

    const infoProcess = spawn('yt-dlp', [
        '--dump-json',
        url
    ]);

    let output = '';
    let stderrOutput = '';
    let processFinished = false;

    // req.on('close', () => {
    //     if (!processFinished) {
    //         infoProcess.kill('SIGKILL');
    //     }
    // });

    infoProcess.stdout.on('data', (data) => {
        output += data.toString();
    });

    infoProcess.stderr.on('data', data => {
        stderrOutput += data.toString();
        console.log(`[yt-dlp info stderr]: ${data.toString()}`);
    });

    infoProcess.on('error', (err) => {
        processFinished = true;
        console.error('[Info Error]:', err);
        if (!res.headersSent) {
            return res.status(500).json({ 
                error: 'Failed to retrieve audio info',
                details: err.message 
            });
        }
    });

    infoProcess.on('close', (code) => {
        processFinished = true;

        if (code !== 0) {
            if (!res.headersSent) {
                return res.status(500).json({ 
                    error: 'Failed process yt-dlp info',
                    details: stderrOutput.trim() 
                });
            }
            return;
        }

        try {
            const json = JSON.parse(output);
            res.json(json);
        } catch (err) {
            if (!res.headersSent) {
                res.status(500).json({ error: 'Invalid JSON response from yt-dlp' });
            }
        }
    });
});

witubeServer.listen(PORT, () => {
    console.log(`Server listening on port ${PORT}`);
});
