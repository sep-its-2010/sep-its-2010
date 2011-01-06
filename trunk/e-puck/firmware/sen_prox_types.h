#ifndef sen_prox_types_h__
#define sen_prox_types_h__

#include "common.h"

enum {
	SEN_PROX_NUM_SENSORS = 8 ///< Specifies the amount of IR proximity sensors.
};


/*!
 * \brief
 * Specifies the container of the IR proximity sensor values.
 */
typedef struct {
	uint8_t aui8Data[SEN_PROX_NUM_SENSORS]; ///< Holds the value of each IR proximity sensor.
} sen_prox_SData_t;

#endif // sen_prox_types_h__
