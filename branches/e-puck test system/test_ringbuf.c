#include <string.h>
#include <stdio.h>

#include "hal_uart1.h"
#include "ringbuf.h"

#include "test_ringbuf.h"


void test_ringbuf_run( void) {

	hal_uart1_puts( "\r\nTesting ring buffer:\r\n\r\n");

	uint8_t aui8Buffer[32];
	ringbuf_SContext_t podBuf;
	ringbuf_init( &podBuf, aui8Buffer, sizeof( aui8Buffer));

	char cstrPrint[128];
	hal_uart1_puts( "Initializing ...\r\nIs empty: ");
	hal_uart1_puts( ringbuf_isEmpty( &podBuf) ? "True\r\n" : "False\r\n");
	sprintf( cstrPrint, "Size: %u  Usage: %u  Free: %u\r\n", ringbuf_getSize( &podBuf), ringbuf_getUsage( &podBuf), ringbuf_getFree( &podBuf));
	hal_uart1_puts( cstrPrint);

	hal_uart1_puts( "\r\nPushing letters 'a' to 'z' ...\r\nIs empty: ");
	for( uint8_t ui8 = 'a'; ui8 <= 'z'; ui8++) {
		ringbuf_push( &podBuf, ui8);
	}
	hal_uart1_puts( ringbuf_isEmpty( &podBuf) ? "True\r\n" : "False\r\n");
	sprintf( cstrPrint, "Size: %u  Usage: %u  Free: %u\r\n", ringbuf_getSize( &podBuf), ringbuf_getUsage( &podBuf), ringbuf_getFree( &podBuf));
	hal_uart1_puts( cstrPrint);

	hal_uart1_puts( "\r\nPushing 6 digits ('0' to '5') ...\r\nIs full: ");
	for( uint8_t ui8 = '0'; ui8 <= '5'; ui8++) {
		ringbuf_push( &podBuf, ui8);
	}
	hal_uart1_puts( ringbuf_isFull( &podBuf) ? "True\r\n" : "False\r\n");
}
