#ifndef subs_types_h__
#define subs_types_h__

typedef void (*subs_fnReset_t)( void);
typedef bool (*subs_fnRun_t)( void);

typedef struct subs_SBehaviorPriorityList {
	subs_fnReset_t fnReset;
	subs_fnRun_t fnRun;
	struct subs_SBehaviorPriorityList* lppodNext;
	uint8_t ui16Priotity;	
} subs_SBehaviorPriorityList_t;

#endif /* subs_types_h__ */
