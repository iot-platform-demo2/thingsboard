///
/// Copyright © 2016-2026 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

/**
 * Flot re-exports legacy paths via package.json "exports". TypeScript with
 * moduleResolution "node" does not resolve those; these declarations satisfy
 * the compiler for dynamic imports used at runtime by the bundler.
 */
declare module 'flot/src/jquery.flot.js';
declare module 'flot/lib/jquery.colorhelpers.js';
declare module 'flot/src/plugins/jquery.flot.time.js';
declare module 'flot/src/plugins/jquery.flot.selection.js';
declare module 'flot/src/plugins/jquery.flot.pie.js';
declare module 'flot/src/plugins/jquery.flot.crosshair.js';
declare module 'flot/src/plugins/jquery.flot.stack.js';
declare module 'flot/src/plugins/jquery.flot.symbol.js';
