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
	const char *Subtopics[] = {"/deviceSub/fanspeed"};	//��������
	const char *Pubtopics = {"/devicePub/fanspeed	"};		//��������
	
	unsigned short timeCount = 0;	//���ͼ������
	
	unsigned char *dataPtr = NULL;

	uint8_t speed = 30;
	char pub_buf[100];
	
	OLED_Init();
	Motor_Init();
	
	NVIC_PriorityGroupConfig(NVIC_PriorityGroup_2);
	Usart1_Init(115200);							//����1����ӡ��Ϣ
	Usart2_Init(115200);							//����2������ESP8266
	UsartPrintf(USART_DEBUG, " Hardware init OK\r\n");
	
	ESP8266_Init();
	
	OneNet_DevLink();		//����OneNET
	Delay_ms(250);

	OneNet_Subscribe(Subtopics, 1);
	
	while(1)
	{
		if(++timeCount >= 500)									//���ͼ��5s
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
