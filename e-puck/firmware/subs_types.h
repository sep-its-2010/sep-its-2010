#ifndef subs_types_h__
#define subs_types_h__

/*!
 * \brief
 * Holds a subsumption reset callback.
 * 
 * \see
 * subs_fnRun_t | subs_register | subs_reset
 */
typedef void (*subs_fnReset_t)( void);


/*!
 * \brief
 * Holds a subsumption action callback.
 *
 * \remarks
 * The function must return true if the associated behavior triggered and false otherwise.
 * 
 * \see
 * subs_fnReset_t | subs_register | subs_unregister
 */
typedef bool (*subs_fnRun_t)( void);


/*!
 * \brief
 * A subsumption behavior list node.
 * 
 * The list node is to be used for single linked priority lists.
 * 
 * \see
 * subs_init | subs_register | subs_unregister.
 */
typedef struct subs_SBehaviorPriorityList {
	subs_fnReset_t fnReset; ///< Holds an optional reset callback.
	subs_fnRun_t fnRun; ///< Holds the required action callback.
	struct subs_SBehaviorPriorityList* lppodNext; ///< Holds a pointer to the next node.
	uint16_t ui16Priotity; ///< Holds the priority of this node.
} subs_SBehaviorPriorityList_t;

#endif /* subs_types_h__ */
