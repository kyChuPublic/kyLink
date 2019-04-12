################################################################################
#  subdir.mk
# Created on: Feb 25, 2019
#     Author: kychu
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
./apps/com_task.c \
./apps/example.c \
./apps/terminal.c \
./apps/uart.c 

OBJS += \
$(BuildPath)/apps/com_task.o \
$(BuildPath)/apps/example.o \
$(BuildPath)/apps/terminal.o \
$(BuildPath)/apps/uart.o 

C_DEPS += \
$(BuildPath)/apps/com_task.d \
$(BuildPath)/apps/example.d \
$(BuildPath)/apps/terminal.d \
$(BuildPath)/apps/uart.d 

OBJ_DIRS = $(sort $(dir $(OBJS)))

# Each subdirectory must supply rules for building sources it contributes
$(BuildPath)/apps/%.o: ./apps/%.c | $(OBJ_DIRS)
	@echo ' CC $<'
	@gcc $(INCS) -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"

