export interface UserDTO {
  id: number;
  nickname: string;
  name: string;
  email?: string;
  roles: string[];
  rolesString: string;
  submissions: number;
  contestsParticipated: number;
  acceptedSubmissions: number;

}