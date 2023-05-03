#include "stm32f10x.h"                  // Device header
#include "OLED.h"
#include "delay.h"
#include "Motor.h"
#include "usart.h"
#include "onenet.h"
#include "esp8266.h"
#include <stdio.h>

int main(void)
{
	const char *Subtopics[] = {"/deviceSub/fanspeed"};	//订阅数据
	const char *Pubtopics = {"/devicePub/fanspeed	"};		//发布数据
	
	unsigned short timeCount = 0;	//发送间隔变量
	
	unsigned char *dataPtr = NULL;

	uint8_t speed = 30;
	char pub_buf[100];
	
	OLED_Init();
	Motor_Init();
	
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);
	Usart1_Init(115200);							//串口1，打印信息
	Usart2_Init(115200);							//串口2，驱动ESP8266
	UsartPrintf(USART_DEBUG, " Hardware init OK\r\n");
	
	ESP8266_Init();
	
	OneNet_DevLink();		//接入OneNET
	Delay_ms(250);

	OneNet_Subscribe(Subtopics, 1);
	
	while(1)
	{
		if(++timeCount >= 500)									//发送间隔5s
		{
			UsartPrintf(USART_DEBUG, "OneNet_Publish\r\n");
			sprintf(pub_buf,"{\"fanspeed\":%d}",speed);
			OneNet_Publish(Pubtopics, pub_buf);
			timeCount = 0;
			ESP8266_Clear();
		}
		
		dataPtr = ESP8266_GetIPD(3);
		if(dataPtr != NULL)
			OneNet_RevPro(dataPtr);
		
		Delay_ms(10);
	
	}
}
