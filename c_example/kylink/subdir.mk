################################################################################
#  subdir.mk
# Created on: Feb 25, 2019
#     Author: kychu
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
./kylink/kyLink.c 

OBJS += \
$(BuildPath)/kylink/kyLink.o 

C_DEPS += \
$(BuildPath)/kylink/kyLink.d 

OBJ_DIRS = $(sort $(dir $(OBJS)))

# Each subdirectory must supply rules for building sources it contributes
$(BuildPath)/kylink/%.o: ./kylink/%.c | $(OBJ_DIRS)
	@echo ' CC $<'
	@gcc $(INCS) -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"

