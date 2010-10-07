#include <p30f6014A.h>

#include <motor_led/e_epuck_ports.h>
#include <motor_led/e_init_port.h>
#include <motor_led/advance_one_timer/e_agenda.h>
#include <motor_led/advance_one_timer/e_motors.h>
#include <uart/e_uart_char.h>
#include <I2C/e_I2C_master_module.h>
#include <I2C/e_I2C_protocol.h>
#include <time.h>

#define uart_send_text(msg) do { e_send_uart1_char(msg,strlen(msg)); while(e_uart1_sending()); } while(0)

#define T 20
#define INITIAL_SPEED 500

void initTMR1(void);
void __attribute__((interrupt, auto_psv, shadow)) _T1Interrupt(void);

int getSelector() {
	return SELECTOR0 + 2*SELECTOR1 + 4*SELECTOR2 + 8*SELECTOR3;
}

int main(void)
{
	char buffer[30];
	long i = 0;

	e_init_port();
	e_init_uart1();
	e_init_motors();	
	e_i2cp_init();

	e_set_speed(0, 0);
	e_start_agendas_processing();
   	initTMR1();
	
	BODY_LED = BODY_LED^1;
	int led_active = 0;

	while(1)
		{
		LED0 = LED1 = LED2 = LED3 = LED4 = LED5 = LED6 = LED7 = 0;
		switch(led_active){
			case 0:
				LED0 = 1;
				break;
			case 1:
				LED1 = 1;
				break;
			case 2:
				LED2 = 1;
				break;
			case 3:
				LED3 = 1;
				break;
			case 4:
				LED4 = 1;
				break;
			case 5:
				LED5 = 1;
				break;
			case 6:
				LED6 = 1;
				break;
			case 7:
				LED7 = 1;
				break;	
		}
		
		led_active = led_active + 1;
		led_active = led_active % 8;
		
		for(i=0;i<100000;i++)
			asm("nop");
		}
}


// timer 1 interrupt
// this code is executed every COM_TIME ms
void __attribute__((interrupt, auto_psv, shadow)) _T1Interrupt(void) {
	
	IFS0bits.T1IF = 0; // clear interrupt flag

	const unsigned char I2C_address = 0xC0;
	const long t = T/1000;
	const long kr = 4, Tn = 0.4, Tv = 0.0;
	const long yp = 1 + (Tv / t);
	const long yi = 1 - (t / Tn) + 2 * (Tv / t);
	const long yd = (Tv / t);

	char buffer[30];
	int j;
	int l_buffer[3];
	long y_new = 0, x_new = 0;
	static int	l_calibrate[3] = {0,0,0};
	static long y_old = 0, x_old = 0, x_old_old = 0;

	// selector 0 = calibrate
	// selector else = drive
	int selector = getSelector();

	if (!(selector == 0 && l_calibrate[0] != 0)) {
		
		// read ground-sensor-data
		e_i2cp_enable();
        for(j = 0; j < 6; j++) {
          if (j % 2 == 0) buffer[j] = e_i2cp_read(I2C_address, j + 1);
          else            buffer[j] = e_i2cp_read(I2C_address, j - 1);
        }
        e_i2cp_disable();
		l_buffer[0] = (unsigned int) (buffer[0] & 0xff) + ((unsigned int) buffer[1] << 8);
		l_buffer[1] = (unsigned int) (buffer[2] & 0xff) + ((unsigned int) buffer[3] << 8);
		l_buffer[2] = (unsigned int) (buffer[4] & 0xff) + ((unsigned int) buffer[5] << 8);

		if (selector == 0) {
			l_calibrate[0] = l_buffer[0];
			l_calibrate[1] = l_buffer[1];
			l_calibrate[2] = l_buffer[2];
			return;
		}

		// read diff		
		x_new = l_buffer[2] - l_buffer[0] - (l_calibrate[2] - l_calibrate[0]);

		// PID-speed-algorithm
		y_new = kr * (yp * x_new - yi * x_old + yd * x_old_old) + y_old;	
		
		// set motor speed
		e_set_speed(INITIAL_SPEED, (int)(y_new / 2));
					
		x_old_old = x_old;
		x_old = x_new;
		y_old = y_new;
	}
}

/* init the Timer 1 */
void initTMR1(void) {
	T1CON = 0;
	T1CONbits.TCKPS = 3; // prescsaler = 256
	TMR1 = 0; // clear timer 1
	PR1 = (T*MILLISEC)/256.0; // interrupt after T ms
	IFS0bits.T1IF = 0; // clear interrupt flag
	IEC0bits.T1IE = 1; // set interrupt enable bit
	T1CONbits.TON = 1; // start Timer1
}
