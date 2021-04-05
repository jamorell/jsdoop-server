
//const tf = require('@tensorflow/tfjs');
const tfjsnpy = require('tfjs-npy');
const npyparse = tfjsnpy.parse;
const npyserialize = tfjsnpy.serialize;


const npyjs = require('npyjs');

window.npyparse = npyparse;
window.npyserialize = npyserialize;
window.npyjs = npyjs;