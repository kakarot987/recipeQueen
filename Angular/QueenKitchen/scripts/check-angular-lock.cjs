'use strict';
const fs = require('node:fs');
const path = require('node:path');
const lock = JSON.parse(fs.readFileSync(path.join(__dirname, '..', 'package-lock.json'), 'utf8'));
const framework = [
  '@angular/common','@angular/compiler','@angular/core','@angular/forms',
  '@angular/platform-browser','@angular/platform-browser-dynamic',
  '@angular/router','@angular/compiler-cli'
];
const resolved = framework.map(name => [name,lock.packages['node_modules/'+name]?.version]);
if (resolved.some(([,version])=>!version) || new Set(resolved.map(([,v])=>v)).size !== 1) {
  console.error('Angular exact peer versions are inconsistent:', resolved);
  process.exit(1);
}
console.log('Angular framework peers aligned:', resolved[0][1]);
