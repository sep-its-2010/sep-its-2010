;********************************************************************
;	Tiny Bootloader		dsPIC series		Size=100words
;	claudiu.chiculita@ugal.ro
;	http://www.etc.ugal.ro/cchiculita/software/picbootloader.htm
;********************************************************************
        .equ __30F6014a, 1
        .include "p30f6014a.inc"

config          __FOSC, CSW_FSCM_OFF & XT_PLL16
config          __FWDT, WDT_OFF
config          __FBORPOR, PBOR_OFF & MCLR_EN
config          __FGS, CODE_PROT_OFF

.equ Fcy, ( 7372800 * 4)
.equ baud, 115200
.equ IdTypePIC, 0x71
.equ max_flash, 0x18000
	
.global __reset

.section .nbss, bss, near
buff:
	.space 4
buffer:
	.space 128 + 5

;REGISTRY:
;W0	imediat
;W1	Receive
;W2	SendL		need?
;W3	count rcv
;W4	memory buffer pointer
;W5	^
;W6	for
;W7
;W8
;W9
;W10	delay1
;W11	delay2
;W12	EA Write 
;W13	crc
;W14	void(black hole)
;W15	stack

.macro SendL car
	mov.b \car, W2
	mov W2, U1TXREG
.endm

.text
	; Required by the PC side of TinyBootloader
	.org max_flash - 0x100 - ( ( 32 * 3 + 4) * 2)

first_address:
	clr W0
	clr W0
	clr W0
	clr W0

__reset:
BootloaderEntry:
    mov #__SP_init, W15
    mov #__SPLIM_init, W0
    mov W0, SPLIM
    nop

_init_uart1:
	;Initialize and Enable UART1 for Tx and Rx
	mov #0x8000, W0			;bit15 = UARTEN
	mov W0, U1MODE
	mov #0x0400, W0			;bit10 = UTXEN
	mov W0, U1STA
    mov #( ( ( Fcy/baud) / 16) - 1), W0
    mov w0, U1BRG
		
	;wait for computer
	mov #buff, W4
	rcall Receive
	sub.b #0xC1, W1			;Expect C1h
	bra NZ, way_to_exit
	SendL #IdTypePIC		;send PIC type
MainLoop:
	SendL #'K'				; "-Everything OK, ready and waiting."

mainl:
	clr W13					;chksum
	mov #buff, W4

	mov #4, W6
dorcv4:
	rcall Receive
	dec W6, W6
	bra NZ, dorcv4
	mov buff, W12
	mov buff + 2, W3
	mov W3, TBLPAG

	lsr W3,#8,W3			; test FF in High High byte of address
	sub.b W3, #0xFF
	bra Z, writeOK 


	mov #(96+1), W6
dorcvB:	rcall Receive
	dec W6, W6
	bra NZ, dorcvB

	cp0 W13 				;check crc
	bra Z, chksumok
ziieroare: 					;CRC failed
	SendL #'N'
	bra mainl

chksumok:
	; Erase
	tblwtl W0, [W12]
	mov #0x4041, W0
	clrwdt
	rcall Key_Sequence

	mov #buffer, W4
	DO #31, edtbl3
	tblwtl.b [W4++], [W12++]
	tblwtl.b [W4++], [W12--]
	tblwth.b [W4++], [W12]
edtbl3:
	inc2 W12, W12
	mov #0x4001, W0
	rcall Key_Sequence
	bra MainLoop

Key_Sequence:				; Expects a NVMCON value in W0
	mov W0, NVMCON			
	mov #0x55, W0			; Write the key sequence
	mov W0, NVMKEY
	mov #0xAA, W0
	mov W0, NVMKEY
	bset NVMCON, #WR		; Start the write cycle
	nop
	nop
	return
	
Receive:
	; 5 instructions per inner cycle; 65536 inner cycles; 1sec timeout
	mov #( Fcy / ( 5 * 65536)), W10 ; outer loops
rpt2:
	mov #0, W11
rpt1:
	btss U1STA, #URXDA
	bra norcv
	mov U1RXREG, W1			;return read data in W
	mov.b W1, [W4++]
	add.b W1, W13, W13		;compute crc
	return
norcv:
	clrwdt
	dec W11, W11
	bra NZ, rpt1
	dec W10, W10
	bra NZ, rpt2
timeout:
way_to_exit:
	clr U1MODE				; deactivate UART
	bra first_address

writeOK:
	bset LATA, #14
	bclr TRISA, #14
	cp0	W12
	bra Z, writeOK
loopEnd1:
	mov #0xFFFF, W0
loopEnd2:
	dec	W0, W0
	bra NZ, loopEnd2
	dec W12, W12
	bra NZ, loopEnd1

endLoop:
	bclr LATA, #14

	bra way_to_exit		 
.end



