#ifndef hal_int_h__
#define hal_int_h__

#include "common.h"

#include "hal_int_types.h"

static inline void hal_int_enable(
	IN const hal_int_ESource_t _eSource
	);

static inline void hal_int_disable(
	IN const hal_int_ESource_t _eSource
	);

static inline void hal_int_setPriority(
	IN const hal_int_ESource_t _eSource,
	IN const hal_int_EPriority_t _ePriority
	);

static inline bool hal_int_isFlagSet(
	IN const hal_int_ESource_t _eSource
	);

static inline void hal_int_clearFlag(
	IN const hal_int_ESource_t _eSource
	);


void hal_int_enable(
	IN const hal_int_ESource_t _eSource
	) {

	switch( _eSource) {
		case HAL_INT_SOURCE__ADC_COMPLETE: {
			IEC0bits.ADIE = true;
			break;
		}
		case HAL_INT_SOURCE__BROWNOUT: {
			IEC2bits.LVDIE = true;
			break;
		}
		case HAL_INT_SOURCE__CAN1: {
			IEC1bits.C1IE = true;
			break;
		}
		case HAL_INT_SOURCE__CAN2: {
			IEC2bits.C2IE = true;
			break;
		}
		case HAL_INT_SOURCE__DATA_CONVERTER: {
			IEC2bits.DCIIE = true;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL0: {
			IEC0bits.INT0IE = true;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL1: {
			IEC1bits.INT1IE = true;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL2: {
			IEC1bits.INT2IE = true;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL3: {
			IEC2bits.INT3IE = true;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL4: {
			IEC2bits.INT4IE = true;
			break;
		}
		case HAL_INT_SOURCE__I2C_BUS_COLLISION: {
			IEC0bits.MI2CIE = true;
			break;
		}
		case HAL_INT_SOURCE__I2C_TRANSFER_COMPLETE: {
			IEC0bits.SI2CIE = true;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN1: {
			IEC0bits.IC1IE = true;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN2: {
			IEC0bits.IC2IE = true;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN3: {
			IEC1bits.IC3IE = true;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN4: {
			IEC1bits.IC4IE = true;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN5: {
			IEC1bits.IC5IE = true;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN6: {
			IEC1bits.IC6IE = true;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN7: {
			IEC1bits.IC7IE = true;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN8: {
			IEC1bits.IC8IE = true;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CHANGED: {
			IEC0bits.CNIE = true;
			break;
		}
		case HAL_INT_SOURCE__MEMORY_WRITE_COMPLETE: {
			IEC0bits.NVMIE = true;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN1: {
			IEC0bits.OC1IE = true;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN2: {
			IEC0bits.OC2IE = true;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN3: {
			IEC1bits.OC3IE = true;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN4: {
			IEC1bits.OC4IE = true;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN5: {
			IEC2bits.OC5IE = true;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN6: {
			IEC2bits.OC6IE = true;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN7: {
			IEC2bits.OC7IE = true;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN8: {
			IEC2bits.OC8IE = true;
			break;
		}
		case HAL_INT_SOURCE__SPI1: {
			IEC0bits.SPI1IE = true;
			break;
		}
		case HAL_INT_SOURCE__SPI2: {
			IEC1bits.SPI2IE = true;
			break;
		}
		case HAL_INT_SOURCE__TIMER1: {
			IEC0bits.T1IE = true;
			break;
		}
		case HAL_INT_SOURCE__TIMER2: {
			IEC0bits.T2IE = true;
			break;
		}
		case HAL_INT_SOURCE__TIMER3: {
			IEC0bits.T3IE = true;
			break;
		}
		case HAL_INT_SOURCE__TIMER4: {
			IEC1bits.T4IE = true;
			break;
		}
		case HAL_INT_SOURCE__TIMER5: {
			IEC1bits.T5IE = true;
			break;
		}
		case HAL_INT_SOURCE__UART1_RECEIVER: {
			IEC0bits.U1RXIE = true;
			break;
		}
		case HAL_INT_SOURCE__UART1_TRANSMITTER: {
			IEC0bits.U1TXIE = true;
			break;
		}
		case HAL_INT_SOURCE__UART2_RECEIVER: {
			IEC1bits.U2RXIE = true;
			break;
		}
		case HAL_INT_SOURCE__UART2_TRANSMITTER: {
			IEC1bits.U2TXIE = true;
			break;
		}
		default: {
	
		}
	}
}

void hal_int_disable(
	IN const hal_int_ESource_t _eSource
	) {

	switch( _eSource) {
		case HAL_INT_SOURCE__ADC_COMPLETE: {
			IEC0bits.ADIE = false;
			break;
		}
		case HAL_INT_SOURCE__BROWNOUT: {
			IEC2bits.LVDIE = false;
			break;
		}
		case HAL_INT_SOURCE__CAN1: {
			IEC1bits.C1IE = false;
			break;
		}
		case HAL_INT_SOURCE__CAN2: {
			IEC2bits.C2IE = false;
			break;
		}
		case HAL_INT_SOURCE__DATA_CONVERTER: {
			IEC2bits.DCIIE = false;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL0: {
			IEC0bits.INT0IE = false;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL1: {
			IEC1bits.INT1IE = false;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL2: {
			IEC1bits.INT2IE = false;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL3: {
			IEC2bits.INT3IE = false;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL4: {
			IEC2bits.INT4IE = false;
			break;
		}
		case HAL_INT_SOURCE__I2C_BUS_COLLISION: {
			IEC0bits.MI2CIE = false;
			break;
		}
		case HAL_INT_SOURCE__I2C_TRANSFER_COMPLETE: {
			IEC0bits.SI2CIE = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN1: {
			IEC0bits.IC1IE = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN2: {
			IEC0bits.IC2IE = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN3: {
			IEC1bits.IC3IE = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN4: {
			IEC1bits.IC4IE = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN5: {
			IEC1bits.IC5IE = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN6: {
			IEC1bits.IC6IE = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN7: {
			IEC1bits.IC7IE = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN8: {
			IEC1bits.IC8IE = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CHANGED: {
			IEC0bits.CNIE = false;
			break;
		}
		case HAL_INT_SOURCE__MEMORY_WRITE_COMPLETE: {
			IEC0bits.NVMIE = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN1: {
			IEC0bits.OC1IE = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN2: {
			IEC0bits.OC2IE = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN3: {
			IEC1bits.OC3IE = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN4: {
			IEC1bits.OC4IE = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN5: {
			IEC2bits.OC5IE = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN6: {
			IEC2bits.OC6IE = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN7: {
			IEC2bits.OC7IE = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN8: {
			IEC2bits.OC8IE = false;
			break;
		}
		case HAL_INT_SOURCE__SPI1: {
			IEC0bits.SPI1IE = false;
			break;
		}
		case HAL_INT_SOURCE__SPI2: {
			IEC1bits.SPI2IE = false;
			break;
		}
		case HAL_INT_SOURCE__TIMER1: {
			IEC0bits.T1IE = false;
			break;
		}
		case HAL_INT_SOURCE__TIMER2: {
			IEC0bits.T2IE = false;
			break;
		}
		case HAL_INT_SOURCE__TIMER3: {
			IEC0bits.T3IE = false;
			break;
		}
		case HAL_INT_SOURCE__TIMER4: {
			IEC1bits.T4IE = false;
			break;
		}
		case HAL_INT_SOURCE__TIMER5: {
			IEC1bits.T5IE = false;
			break;
		}
		case HAL_INT_SOURCE__UART1_RECEIVER: {
			IEC0bits.U1RXIE = false;
			break;
		}
		case HAL_INT_SOURCE__UART1_TRANSMITTER: {
			IEC0bits.U1TXIE = false;
			break;
		}
		case HAL_INT_SOURCE__UART2_RECEIVER: {
			IEC1bits.U2RXIE = false;
			break;
		}
		case HAL_INT_SOURCE__UART2_TRANSMITTER: {
			IEC1bits.U2TXIE = false;
			break;
		}
		default: {
	
		}
	}
}

void hal_int_setPriority(
	IN const hal_int_ESource_t _eSource,
	IN const hal_int_EPriority_t _ePriority
	) {

	switch( _eSource) {
		case HAL_INT_SOURCE__ADC_COMPLETE: {
			IPC2bits.ADIP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__BROWNOUT: {
			IPC10bits.LVDIP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__CAN1: {
			IPC6bits.C1IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__CAN2: {
			IPC9bits.C2IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__DATA_CONVERTER: {
			IPC10bits.DCIIP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL0: {
			IPC0bits.INT0IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL1: {
			IPC4bits.INT1IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL2: {
			IPC5bits.INT2IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL3: {
			IPC9bits.INT3IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL4: {
			IPC9bits.INT4IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__I2C_BUS_COLLISION: {
			IPC3bits.MI2CIP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__I2C_TRANSFER_COMPLETE: {
			IPC3bits.SI2CIP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN1: {
			IPC0bits.IC1IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN2: {
			IPC1bits.IC2IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN3: {
			IPC7bits.IC3IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN4: {
			IPC7bits.IC4IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN5: {
			IPC7bits.IC5IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN6: {
			IPC7bits.IC6IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN7: {
			IPC4bits.IC7IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN8: {
			IPC4bits.IC8IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CHANGED: {
			IPC3bits.CNIP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__MEMORY_WRITE_COMPLETE: {
			IPC3bits.NVMIP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN1: {
			IPC0bits.OC1IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN2: {
			IPC1bits.OC2IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN3: {
			IPC4bits.OC3IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN4: {
			IPC5bits.OC4IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN5: {
			IPC8bits.OC5IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN6: {
			IPC8bits.OC6IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN7: {
			IPC8bits.OC7IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN8: {
			IPC8bits.OC8IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__SPI1: {
			IPC2bits.SPI1IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__SPI2: {
			IPC6bits.SPI2IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__TIMER1: {
			IPC0bits.T1IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__TIMER2: {
			IPC1bits.T2IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__TIMER3: {
			IPC1bits.T3IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__TIMER4: {
			IPC5bits.T4IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__TIMER5: {
			IPC5bits.T5IP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__UART1_RECEIVER: {
			IPC2bits.U1RXIP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__UART1_TRANSMITTER: {
			IPC2bits.U1TXIP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__UART2_RECEIVER: {
			IPC6bits.U2RXIP = _ePriority;
			break;
		}
		case HAL_INT_SOURCE__UART2_TRANSMITTER: {
			IPC6bits.U2TXIP = _ePriority;
			break;
		}
		default: {
	
		}
	}
}


bool hal_int_isFlagSet(
	IN const hal_int_ESource_t _eSource
	) {

	bool blReturn;

	switch( _eSource) {
		case HAL_INT_SOURCE__ADC_COMPLETE: {
			blReturn = IFS0bits.ADIF;
			break;
		}
		case HAL_INT_SOURCE__BROWNOUT: {
			blReturn = IFS2bits.LVDIF;
			break;
		}
		case HAL_INT_SOURCE__CAN1: {
			blReturn = IFS1bits.C1IF;
			break;
		}
		case HAL_INT_SOURCE__CAN2: {
			blReturn = IFS2bits.C2IF;
			break;
		}
		case HAL_INT_SOURCE__DATA_CONVERTER: {
			blReturn = IFS2bits.DCIIF;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL0: {
			blReturn = IFS0bits.INT0IF;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL1: {
			blReturn = IFS1bits.INT1IF;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL2: {
			blReturn = IFS1bits.INT2IF;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL3: {
			blReturn = IFS2bits.INT3IF;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL4: {
			blReturn = IFS2bits.INT4IF;
			break;
		}
		case HAL_INT_SOURCE__I2C_BUS_COLLISION: {
			blReturn = IFS0bits.MI2CIF;
			break;
		}
		case HAL_INT_SOURCE__I2C_TRANSFER_COMPLETE: {
			blReturn = IFS0bits.SI2CIF;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN1: {
			blReturn = IFS0bits.IC1IF;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN2: {
			blReturn = IFS0bits.IC2IF;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN3: {
			blReturn = IFS1bits.IC3IF;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN4: {
			blReturn = IFS1bits.IC4IF;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN5: {
			blReturn = IFS1bits.IC5IF;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN6: {
			blReturn = IFS1bits.IC6IF;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN7: {
			blReturn = IFS1bits.IC7IF;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN8: {
			blReturn = IFS1bits.IC8IF;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CHANGED: {
			blReturn = IFS0bits.CNIF;
			break;
		}
		case HAL_INT_SOURCE__MEMORY_WRITE_COMPLETE: {
			blReturn = IFS0bits.NVMIF;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN1: {
			blReturn = IFS0bits.OC1IF;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN2: {
			blReturn = IFS0bits.OC2IF;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN3: {
			blReturn = IFS1bits.OC3IF;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN4: {
			blReturn = IFS1bits.OC4IF;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN5: {
			blReturn = IFS2bits.OC5IF;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN6: {
			blReturn = IFS2bits.OC6IF;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN7: {
			blReturn = IFS2bits.OC7IF;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN8: {
			blReturn = IFS2bits.OC8IF;
			break;
		}
		case HAL_INT_SOURCE__SPI1: {
			blReturn = IFS0bits.SPI1IF;
			break;
		}
		case HAL_INT_SOURCE__SPI2: {
			blReturn = IFS1bits.SPI2IF;
			break;
		}
		case HAL_INT_SOURCE__TIMER1: {
			blReturn = IFS0bits.T1IF;
			break;
		}
		case HAL_INT_SOURCE__TIMER2: {
			blReturn = IFS0bits.T2IF;
			break;
		}
		case HAL_INT_SOURCE__TIMER3: {
			blReturn = IFS0bits.T3IF;
			break;
		}
		case HAL_INT_SOURCE__TIMER4: {
			blReturn = IFS1bits.T4IF;
			break;
		}
		case HAL_INT_SOURCE__TIMER5: {
			blReturn = IFS1bits.T5IF;
			break;
		}
		case HAL_INT_SOURCE__UART1_RECEIVER: {
			blReturn = IFS0bits.U1RXIF;
			break;
		}
		case HAL_INT_SOURCE__UART1_TRANSMITTER: {
			blReturn = IFS0bits.U1TXIF;
			break;
		}
		case HAL_INT_SOURCE__UART2_RECEIVER: {
			blReturn = IFS1bits.U2RXIF;
			break;
		}
		case HAL_INT_SOURCE__UART2_TRANSMITTER: {
			blReturn = IFS1bits.U2TXIF;
			break;
		}
		default: {

		}
	}

	return blReturn;
}


void hal_int_clearFlag(
	IN const hal_int_ESource_t _eSource
	) {

	switch( _eSource) {
		case HAL_INT_SOURCE__ADC_COMPLETE: {
			IFS0bits.ADIF = false;
			break;
		}
		case HAL_INT_SOURCE__BROWNOUT: {
			IFS2bits.LVDIF = false;
			break;
		}
		case HAL_INT_SOURCE__CAN1: {
			IFS1bits.C1IF = false;
			break;
		}
		case HAL_INT_SOURCE__CAN2: {
			IFS2bits.C2IF = false;
			break;
		}
		case HAL_INT_SOURCE__DATA_CONVERTER: {
			IFS2bits.DCIIF = false;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL0: {
			IFS0bits.INT0IF = false;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL1: {
			IFS1bits.INT1IF = false;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL2: {
			IFS1bits.INT2IF = false;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL3: {
			IFS2bits.INT3IF = false;
			break;
		}
		case HAL_INT_SOURCE__EXTERNAL4: {
			IFS2bits.INT4IF = false;
			break;
		}
		case HAL_INT_SOURCE__I2C_BUS_COLLISION: {
			IFS0bits.MI2CIF = false;
			break;
		}
		case HAL_INT_SOURCE__I2C_TRANSFER_COMPLETE: {
			IFS0bits.SI2CIF = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN1: {
			IFS0bits.IC1IF = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN2: {
			IFS0bits.IC2IF = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN3: {
			IFS1bits.IC3IF = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN4: {
			IFS1bits.IC4IF = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN5: {
			IFS1bits.IC5IF = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN6: {
			IFS1bits.IC6IF = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN7: {
			IFS1bits.IC7IF = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CAPTURE_CHAN8: {
			IFS1bits.IC8IF = false;
			break;
		}
		case HAL_INT_SOURCE__INPUT_CHANGED: {
			IFS0bits.CNIF = false;
			break;
		}
		case HAL_INT_SOURCE__MEMORY_WRITE_COMPLETE: {
			IFS0bits.NVMIF = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN1: {
			IFS0bits.OC1IF = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN2: {
			IFS0bits.OC2IF = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN3: {
			IFS1bits.OC3IF = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN4: {
			IFS1bits.OC4IF = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN5: {
			IFS2bits.OC5IF = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN6: {
			IFS2bits.OC6IF = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN7: {
			IFS2bits.OC7IF = false;
			break;
		}
		case HAL_INT_SOURCE__OUTPUT_COMPARE_CHAN8: {
			IFS2bits.OC8IF = false;
			break;
		}
		case HAL_INT_SOURCE__SPI1: {
			IFS0bits.SPI1IF = false;
			break;
		}
		case HAL_INT_SOURCE__SPI2: {
			IFS1bits.SPI2IF = false;
			break;
		}
		case HAL_INT_SOURCE__TIMER1: {
			IFS0bits.T1IF = false;
			break;
		}
		case HAL_INT_SOURCE__TIMER2: {
			IFS0bits.T2IF = false;
			break;
		}
		case HAL_INT_SOURCE__TIMER3: {
			IFS0bits.T3IF = false;
			break;
		}
		case HAL_INT_SOURCE__TIMER4: {
			IFS1bits.T4IF = false;
			break;
		}
		case HAL_INT_SOURCE__TIMER5: {
			IFS1bits.T5IF = false;
			break;
		}
		case HAL_INT_SOURCE__UART1_RECEIVER: {
			IFS0bits.U1RXIF = false;
			break;
		}
		case HAL_INT_SOURCE__UART1_TRANSMITTER: {
			IFS0bits.U1TXIF = false;
			break;
		}
		case HAL_INT_SOURCE__UART2_RECEIVER: {
			IFS1bits.U2RXIF = false;
			break;
		}
		case HAL_INT_SOURCE__UART2_TRANSMITTER: {
			IFS1bits.U2TXIF = false;
			break;
		}
		default: {
	
		}
	}
}

#endif /* hal_int_h__ */
