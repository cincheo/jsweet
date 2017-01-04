
interface Buffer {}

interface SlowBuffer {}

declare module "buffer" {
    export var INSPECT_MAX_BYTES: number;
    var BuffType: typeof Buffer;
    var SlowBuffType: typeof SlowBuffer;
    // selective export (syntax-only supported)
    export { BuffType as Buffer, SlowBuffType as SlowBuffer };
    export { Buffer };
}

