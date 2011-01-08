#include "conquest.h"

#include "conquest_sim.h"

enum {
	DIM_X = 5, ///< Specifies the field width.
	DIM_Y = 5, ///< Specifies the field height.
	INITIAL_POS_X = 0, ///< Specifies the initial x position within the field.
	INITIAL_POS_Y = 0 ///< Specifies the initial y position within the field.
};


static conquest_ENode_t calculateRelativeNodeType(
	IN const conquest_ENode_t _eNodeType,
	IN const conquest_EDirection_t _eDirection
	);


/*!
 * \brief
 * Holds the simulated field.
 */
static const conquest_ENode_t s_aaeMap[DIM_X][DIM_Y] = {
	{ CONQUEST_NODE__DOWN_RIGHT, CONQUEST_NODE__UP_T,	 CONQUEST_NODE__UP_T,	 CONQUEST_NODE__UP_T,	CONQUEST_NODE__DOWN_LEFT },
	{ CONQUEST_NODE__LEFT_T,	 CONQUEST_NODE__CROSS,	 CONQUEST_NODE__DOWN_T,	 CONQUEST_NODE__CROSS,	CONQUEST_NODE__RIGHT_T },
	{ CONQUEST_NODE__LEFT_T,	 CONQUEST_NODE__RIGHT_T, CONQUEST_NODE__INVALID, CONQUEST_NODE__LEFT_T,	CONQUEST_NODE__RIGHT_T },
	{ CONQUEST_NODE__LEFT_T,	 CONQUEST_NODE__CROSS,	 CONQUEST_NODE__UP_T,	 CONQUEST_NODE__CROSS,	CONQUEST_NODE__RIGHT_T },
	{ CONQUEST_NODE__UP_RIGHT,	 CONQUEST_NODE__DOWN_T,	 CONQUEST_NODE__DOWN_T,	 CONQUEST_NODE__DOWN_T,	CONQUEST_NODE__UP_LEFT }
};


/*!
 * \brief
 * Holds the e-puck view direction.
 */
static conquest_EDirection_t s_eDirection = CONQUEST_DIRECTION__UP;


/*!
 * \brief
 * Holds the current x position within the field.
 */
static uint16_t s_ui16PosX = INITIAL_POS_X;


/*!
 * \brief
 * Holds the current y position within the field.
 */
static uint16_t s_ui16PosY = INITIAL_POS_Y;


/*!
 * \brief
 * Rotates a node type relative to a given direction.
 * 
 * \param _eNodeType
 * Specifies the node type to be rotated.
 * 
 * \param _eDirection
 * Specifies the new orientation after the rotation. 
 * 
 * \returns
 * The node type relative to the specified orientation.
 * 
 * The node is rotated by rotating the direction bit mask and converting it back to the node type.
 * 
 */
conquest_ENode_t calculateRelativeNodeType(
	IN const conquest_ENode_t _eNodeType,
	IN const conquest_EDirection_t _eDirection
	) {

	conquest_ENode_t eNode = _eNodeType;

	if( _eDirection != CONQUEST_DIRECTION__UP) {

		// Rotate possible moving directions
		uint16_t ui16RawDirections = _eNodeType & 0xFF;
		if( _eDirection == CONQUEST_DIRECTION__RIGHT) {
			ui16RawDirections <<= 6;
			ui16RawDirections |= ui16RawDirections >> 8;
		} else if( _eDirection == CONQUEST_DIRECTION__DOWN) {
			ui16RawDirections <<= 4;
			ui16RawDirections |= ui16RawDirections >> 8;
		} else if( _eDirection == CONQUEST_DIRECTION__LEFT) {
			ui16RawDirections <<= 2;
			ui16RawDirections |= ui16RawDirections >> 8;
		}

		eNode = conquest_convertDirMaskToNode( ui16RawDirections & 0xFF);
	}

	return eNode;
}


/*!
 * \brief
 * Resets the simulator.
 *
 * The initial position is set to (#INITIAL_POS_X, #INITIAL_POS_Y). The origin is in the top left edge.
 * 
 * \see
 * conquest_sim_getCurrentNode | conquest_sim_move | conquest_sim_turnLeft | conquest_sim_turnRight
 */
void conquest_sim_reset( void) {

	s_eDirection = CONQUEST_DIRECTION__UP;
	s_ui16PosX = INITIAL_POS_X;
	s_ui16PosY = INITIAL_POS_Y;

	conquest_setLastNode( calculateRelativeNodeType( s_aaeMap[s_ui16PosY][s_ui16PosX], s_eDirection));
}


/*!
 * \brief
 * Gets the node type based on the current direction and position.
 * 
 * \returns
 * The current relative node type.
 * 
 * \see
 * conquest_sim_reset | conquest_sim_move | conquest_sim_turnLeft | conquest_sim_turnRight
 */
conquest_ENode_t conquest_sim_getCurrentNode( void) {

	return calculateRelativeNodeType( s_aaeMap[s_ui16PosY][s_ui16PosX], s_eDirection);
}


/*!
 * \brief
 * Advances the simulated e-puck.
 * 
 * \returns
 * The node new relative node type.
 * 
 * If the request is valid, the position is advanced by one step into the current direction.
 * 
 * \see
 * conquest_sim_reset | conquest_sim_getCurrentNode
 */
conquest_ENode_t conquest_sim_move( void) {

	conquest_ENode_t eNode = CONQUEST_NODE__INVALID;

	if( s_aaeMap[s_ui16PosY][s_ui16PosX] & s_eDirection) {
		if( s_eDirection == CONQUEST_DIRECTION__UP && s_ui16PosY) {
			s_ui16PosY--;
			eNode = calculateRelativeNodeType( s_aaeMap[s_ui16PosY][s_ui16PosX], s_eDirection);
		} else if( s_eDirection == CONQUEST_DIRECTION__RIGHT && s_ui16PosX + 1 < DIM_X) {
			s_ui16PosX++;
			eNode = calculateRelativeNodeType( s_aaeMap[s_ui16PosY][s_ui16PosX], s_eDirection);
		} else if( s_eDirection == CONQUEST_DIRECTION__DOWN && s_ui16PosY + 1 < DIM_Y) {
			s_ui16PosY++;
			eNode = calculateRelativeNodeType( s_aaeMap[s_ui16PosY][s_ui16PosX], s_eDirection);
		} else if( s_eDirection == CONQUEST_DIRECTION__LEFT && s_ui16PosX) {
			s_ui16PosX--;
			eNode = calculateRelativeNodeType( s_aaeMap[s_ui16PosY][s_ui16PosX], s_eDirection);
		}
	}

	return eNode;
}


/*!
 * \brief
 * Turns the simulated e-puck to the left.
 * 
 * \returns
 * The node new relative node type.
 * 
 * \see
 * conquest_sim_reset | conquest_sim_turnRight
 */
conquest_ENode_t conquest_sim_turnLeft( void) {

	uint16_t ui16RawDirection = s_eDirection;
	ui16RawDirection <<= 6;
	ui16RawDirection |= ui16RawDirection >> 8;
	s_eDirection = (conquest_EDirection_t)( ui16RawDirection & 0xFF);

	return calculateRelativeNodeType( s_aaeMap[s_ui16PosY][s_ui16PosX], s_eDirection);
}


/*!
 * \brief
 * Turns the simulated e-puck to the right.
 * 
 * \returns
 * The node new relative node type.
 * 
 * \see
 * conquest_sim_reset | conquest_sim_turnLeft
 */
conquest_ENode_t conquest_sim_turnRight( void) {

	uint16_t ui16RawDirection = s_eDirection;
	ui16RawDirection <<= 2;
	ui16RawDirection |= ui16RawDirection >> 8;
	s_eDirection = (conquest_EDirection_t)( ui16RawDirection & 0xFF);

	return calculateRelativeNodeType( s_aaeMap[s_ui16PosY][s_ui16PosX], s_eDirection);
}
